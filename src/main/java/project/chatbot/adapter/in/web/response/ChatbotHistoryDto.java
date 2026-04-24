package project.chatbot.adapter.in.web.response;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import project.chatbot.adapter.out.mongo.ChatbotHistoryDocument;
import project.chatbot.domain.ChatbotHistory;

import java.time.LocalDateTime;
import java.util.Map;

public record ChatbotHistoryDto(
        MessageType type,
        String content,
        Map<String, Object> metadata,
        LocalDateTime createdAt) {

    public static ChatbotHistoryDto of(Message message, Map<String, Object> metadata) {
        return new ChatbotHistoryDto(message.getMessageType(), message.getText(), metadata, LocalDateTime.now());
    }

    public static ChatbotHistoryDto of(ChatbotHistory chatbotHistory) {
        return new ChatbotHistoryDto(chatbotHistory.getType(), chatbotHistory.getText(), null, chatbotHistory.getCreatedAt());
    }

    public static ChatbotHistoryDto of(ChatbotHistoryDocument document) {
        return new ChatbotHistoryDto(document.getMessageType(), document.getContent(), document.getMetadata(), document.getCreatedAt());
    }
}
