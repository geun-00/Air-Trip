package project.chatbot.application.out.command;

import org.springframework.ai.chat.messages.Message;

import java.util.Map;

public interface SaveChatbotHistoryPort {

    void save(String conversationId, Message message, Map<String, Object> metadata);
}
