package project.chatbot.application.memory;

import org.springframework.ai.chat.messages.Message;
import project.chatbot.adapter.in.web.response.ChatbotHistoryDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryChatbotHistoryMemory implements ChatbotHistoryMemory {

    Map<String, List<ChatbotHistoryDto>> chatbotHistoryStore = new ConcurrentHashMap<>();

    @Override
    public void save(String conversationId, Message message, Map<String, Object> metadata) {
        chatbotHistoryStore.putIfAbsent(conversationId, new ArrayList<>());
        chatbotHistoryStore.get(conversationId).add(ChatbotHistoryDto.of(message, metadata));
    }

    @Override
    public List<ChatbotHistoryDto> getMessages(String conversationId) {
        return new ArrayList<>(chatbotHistoryStore.getOrDefault(conversationId, List.of()));
    }
}
