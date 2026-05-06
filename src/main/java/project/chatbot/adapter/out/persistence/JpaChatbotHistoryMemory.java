package project.chatbot.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;
import project.chatbot.application.in.query.model.ChatbotMessageView;
import project.chatbot.application.out.command.SaveChatbotHistoryPort;
import project.chatbot.application.out.query.LoadChatbotHistoryPort;
import project.chatbot.domain.ChatbotHistory;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JpaChatbotHistoryMemory implements SaveChatbotHistoryPort, LoadChatbotHistoryPort {

    private final ChatbotHistoryRepository chatbotHistoryRepository;

    @Override
    public void save(String conversationId, Message message, Map<String, Object> metadata) {
        chatbotHistoryRepository.save(ChatbotHistory.of(conversationId, message));
    }

    @Override
    public List<ChatbotMessageView> getMessages(String conversationId) {
        return chatbotHistoryRepository.findAllByConversationId(conversationId)
                                       .stream()
                                       .map(history -> new ChatbotMessageView(
                                               history.getType(),
                                               history.getText(),
                                               null,
                                               history.getCreatedAt()
                                       ))
                                       .toList();
    }
}
