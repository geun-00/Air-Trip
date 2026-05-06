package project.chatbot.adapter.in.web.response;

import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;
import java.util.Map;

public record ChatbotHistoryResponse(
        MessageType type,
        String content,
        Map<String, Object> metadata,
        LocalDateTime createdAt
) {
}
