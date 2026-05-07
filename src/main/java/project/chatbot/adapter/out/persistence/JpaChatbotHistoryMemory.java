package project.chatbot.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import project.chatbot.application.in.query.model.ChatbotMessageView;
import project.chatbot.application.out.command.SaveChatbotHistoryPort;
import project.chatbot.application.out.query.LoadChatbotHistoryPort;
import project.chatbot.domain.ChatbotHistory;
import project.chatbot.domain.ChatbotMessageType;

import java.util.List;
import java.util.Map;

@Primary
@Component
@RequiredArgsConstructor
public class JpaChatbotHistoryMemory implements SaveChatbotHistoryPort, LoadChatbotHistoryPort {

    private final ChatbotHistoryRepository chatbotHistoryRepository;

    @Override
    public void save(String conversationId, Message message, Map<String, Object> metadata) {
        chatbotHistoryRepository.save(ChatbotHistory.of(
                conversationId,
                ChatbotMessageType.valueOf(message.getMessageType().name()),
                message.getText(),
                metadata
        ));
    }

    @Override
    public List<ChatbotMessageView> getMessages(String conversationId) {
        return chatbotHistoryRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId)
                                       .stream()
                                       .map(history -> new ChatbotMessageView(
                                               MessageType.valueOf(history.getType().name()),
                                               history.getText(),
                                               history.getMetadata(),
                                               history.getCreatedAt()
                                       ))
                                       .toList();
    }
}
