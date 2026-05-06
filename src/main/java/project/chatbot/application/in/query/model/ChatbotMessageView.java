package project.chatbot.application.in.query.model;

import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;
import java.util.Map;

public record ChatbotMessageView(
        MessageType type,
        String content,
        Map<String, Object> metadata,
        LocalDateTime createdAt
) {
}
