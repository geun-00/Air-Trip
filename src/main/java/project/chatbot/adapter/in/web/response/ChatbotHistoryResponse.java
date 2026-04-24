package project.chatbot.adapter.in.web.response;

import org.springframework.ai.chat.messages.MessageType;

public record ChatbotHistoryResponse(
        String content,
        MessageType messageType
) {
}
