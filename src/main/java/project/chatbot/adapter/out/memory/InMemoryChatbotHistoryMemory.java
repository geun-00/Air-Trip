package project.chatbot.adapter.out.memory;

import org.springframework.ai.chat.messages.Message;
import project.chatbot.application.in.query.model.ChatbotMessageView;
import project.chatbot.application.out.command.SaveChatbotHistoryPort;
import project.chatbot.application.out.query.LoadChatbotHistoryPort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryChatbotHistoryMemory implements SaveChatbotHistoryPort, LoadChatbotHistoryPort {

    private final Map<String, List<ChatbotMessageView>> chatbotHistoryStore = new ConcurrentHashMap<>();

    @Override
    public void save(String conversationId, Message message, Map<String, Object> metadata) {
        chatbotHistoryStore.putIfAbsent(conversationId, new ArrayList<>());
        chatbotHistoryStore.get(conversationId)
                           .add(new ChatbotMessageView(
                                   message.getMessageType(),
                                   message.getText(),
                                   metadata,
                                   LocalDateTime.now()
                           ));
    }

    @Override
    public List<ChatbotMessageView> getMessages(String conversationId) {
        return new ArrayList<>(chatbotHistoryStore.getOrDefault(conversationId, List.of()));
    }
}
