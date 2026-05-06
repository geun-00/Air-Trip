package project.chatbot.adapter.out.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import project.chatbot.application.in.query.model.ChatbotMessageView;
import project.chatbot.application.out.command.SaveChatbotHistoryPort;
import project.chatbot.application.out.query.LoadChatbotHistoryPort;

import java.util.List;
import java.util.Map;

@Primary
@Component
@RequiredArgsConstructor
public class MongoChatbotHistoryMemory implements SaveChatbotHistoryPort, LoadChatbotHistoryPort {

    private final ChatbotHistoryMongoRepository chatbotHistoryMongoRepository;

    @Override
    public void save(String conversationId, Message message, Map<String, Object> metadata) {
        ChatbotHistoryDocument document = ChatbotHistoryDocument.of(conversationId, message, metadata);
        chatbotHistoryMongoRepository.save(document);
    }

    @Override
    public List<ChatbotMessageView> getMessages(String conversationId) {
        return chatbotHistoryMongoRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                                            .stream()
                                            .map(document -> new ChatbotMessageView(
                                                    document.getMessageType(),
                                                    document.getContent(),
                                                    document.getMetadata(),
                                                    document.getCreatedAt()
                                            ))
                                            .toList();
    }
}
