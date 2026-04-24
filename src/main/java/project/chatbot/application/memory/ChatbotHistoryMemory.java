package project.chatbot.application.memory;

import org.springframework.ai.chat.messages.Message;
import project.chatbot.adapter.in.web.response.ChatbotHistoryDto;

import java.util.List;
import java.util.Map;

public interface ChatbotHistoryMemory {
    void save(String conversationId, Message message, Map<String, Object> metadata);
    List<ChatbotHistoryDto> getMessages(String conversationId);
}
