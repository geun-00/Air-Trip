package project.chatbot.adapter.out.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.chatbot.adapter.out.ai.model.AccommodationFilterRow;
import project.chatbot.adapter.out.ai.model.AccommodationSearchTool;
import project.chatbot.adapter.out.ai.model.RecommendedAccommodationRow;
import project.chatbot.application.in.command.model.AskChatbotResult;
import project.chatbot.application.out.ai.GenerateChatbotResponsePort;
import project.chatbot.application.out.ai.model.GenerateChatbotResponseCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatbotResponseAiAdapter implements GenerateChatbotResponsePort {

    private static final int RECOMMENDATION_SEARCH_TOP_K = 10;
    private static final double RECOMMENDATION_SIMILARITY_THRESHOLD = 0.3;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatbotMemoryAdvisorProvider memoryAdvisorProvider;

    @Value("${chatbot.system-prompt.recommend-accommodations}")
    private String systemPrompt;

    @Override
    public AskChatbotResult generate(GenerateChatbotResponseCommand command) {
        AccommodationFilterRow filterInfo = getFilterInfoByToolCalling(command.message());
        Filter.Expression expression = getExpression(filterInfo);

        SearchRequest searchRequest = SearchRequest.builder()
                                                   .query(command.message())
                                                   .topK(RECOMMENDATION_SEARCH_TOP_K)
                                                   .filterExpression(expression)
                                                   .similarityThreshold(RECOMMENDATION_SIMILARITY_THRESHOLD)
                                                   .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest)
                                              .stream()
                                              .filter(doc -> {
                                                  String region = filterInfo.region();
                                                  if (region == null || region.isBlank()) {
                                                      return true;
                                                  }
                                                  String address = doc.getMetadata().get("address").toString();
                                                  return address.contains(region);
                                              })
                                              .toList();

        List<RecommendedAccommodationRow> recommendedAccommodations =
                documents.stream()
                         .map(doc -> new RecommendedAccommodationRow(
                                 Long.parseLong(doc.getMetadata().get("accId").toString()),
                                 doc.getMetadata().get("title").toString(),
                                 extractPriceInfo(doc.getMetadata()),
                                 Integer.parseInt(doc.getMetadata().get("maxPeople").toString())
                         ))
                         .toList();

        Map<String, Object> metadata = new HashMap<>();
        if (!recommendedAccommodations.isEmpty()) {
            metadata.put("recommendedAccommodations", recommendedAccommodations);
        }

        CustomMessageChatMemoryAdvisor advisor = memoryAdvisorProvider.get(command.login());
        String textResponse = chatClient.prompt()
                                        .user(command.message())
                                        .system(systemPrompt + "\n\n" + buildSearchContext(recommendedAccommodations))
                                        .advisors(adv -> adv
                                                .advisors(advisor)
                                                .param(ChatMemory.CONVERSATION_ID, command.conversationId())
                                                .param("metadata", metadata)
                                        )
                                        .call()
                                        .content();

        return new AskChatbotResult(textResponse, metadata);
    }

    private AccommodationFilterRow getFilterInfoByToolCalling(String message) {
        String filterExtractionPrompt = """
                 사용자 메시지에서 숙소 검색 필터 정보를 추출하세요.

                규칙: 사용자가 명시하지 않은 값은 추측/가정하지 말고 반드시 null을 사용하세요.

                추출 항목:
                - region: 지역명이 명시된 경우만 (예: "서울", "부산")
                - minPrice: "최소 X원", "X원 이상" 등 최소 가격이 명시된 경우만
                - maxPrice: "최대 X원", "X원 이하" 등 최대 가격이 명시된 경우만
                - peopleCount: "N명", "N인" 등 인원이 명시된 경우만

                지역 추출 규칙:
                - 도시명만 추출하세요 (예: "강원도 강릉" -> "강릉", "서울특별시" -> "서울")
                - "~도", "~시", "특별시", "광역시" 등의 행정구역 접미사는 제거

                예시:
                - "인천에 숙소 추천해줘" -> region="인천", 나머지 모두 null
                - "서울에 5만원 이하 2인 숙소" -> region="서울", maxPrice=50000, peopleCount=2

                사용자 메시지: %s
                """.formatted(message);

        return chatClient.prompt()
                         .user(filterExtractionPrompt)
                         .tools(new AccommodationSearchTool())
                         .call()
                         .entity(AccommodationFilterRow.class);
    }

    private Filter.Expression getExpression(AccommodationFilterRow filterInfo) {
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        List<Filter.Expression> expressions = new ArrayList<>();

        if (filterInfo.minPrice() != null) {
            expressions.add(builder.gte("minPrice", filterInfo.minPrice()).build());
        }
        if (filterInfo.maxPrice() != null) {
            expressions.add(builder.lte("maxPrice", filterInfo.maxPrice()).build());
        }
        if (filterInfo.peopleCount() != null) {
            expressions.add(builder.lte("maxPeople", filterInfo.peopleCount()).build());
        }

        Filter.Expression expression = null;
        if (!expressions.isEmpty()) {
            expression = expressions.getFirst();

            for (int i = 1; i < expressions.size(); i++) {
                expression = builder.and(
                        new FilterExpressionBuilder.Op(expression),
                        new FilterExpressionBuilder.Op(expressions.get(i))
                ).build();
            }
        }

        return expression;
    }

    private String extractPriceInfo(Map<String, Object> metadata) {
        int minPrice = Integer.parseInt(metadata.get("minPrice").toString());
        int maxPrice = Integer.parseInt(metadata.get("maxPrice").toString());

        if (minPrice == maxPrice) {
            return String.format("%,d원", minPrice);
        }

        return String.format("%,d원 ~ %,d원", minPrice, maxPrice);
    }

    private String buildSearchContext(List<RecommendedAccommodationRow> accommodations) {
        if (accommodations.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("""
                [검색된 숙소 정보]
                아래 정보는 추천을 위한 참고 자료이며,
                사용자가 직접 말한 내용이 아닙니다.
                """);

        for (int i = 0; i < accommodations.size(); i++) {
            RecommendedAccommodationRow acc = accommodations.get(i);
            sb.append(String.format(
                    "%d. %s (최대 %d명, 가격 %s)\n",
                    i + 1,
                    acc.title(),
                    acc.maxPeople(),
                    acc.price()
            ));
        }

        sb.append("""
                위 숙소 정보만을 활용하여
                사용자에게 자연스럽게 추천하세요.
                """);

        return sb.toString();
    }
}
