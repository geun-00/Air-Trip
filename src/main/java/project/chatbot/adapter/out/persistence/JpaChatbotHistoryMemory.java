package project.chatbot.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;
import project.chatbot.adapter.in.web.response.ChatbotHistoryDto;
import project.chatbot.application.memory.ChatbotHistoryMemory;
import project.chatbot.domain.ChatbotHistory;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JpaChatbotHistoryMemory implements ChatbotHistoryMemory {

    private final ChatbotHistoryRepository chatbotHistoryRepository;

    @Override
    public void save(String conversationId, Message message, Map<String, Object> metadata) {
        chatbotHistoryRepository.save(ChatbotHistory.of(conversationId, message));
    }

    @Override
    public List<ChatbotHistoryDto> getMessages(String conversationId) {
        return chatbotHistoryRepository.findAllByConversationId(conversationId)
                                       .stream()
                                       .map(ChatbotHistoryDto::of)
                                       .toList();
    }
}
