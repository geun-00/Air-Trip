package project.chatbot.adapter.in.web.response;

import java.util.Map;

public record AskChatbotResponse(
        String textResponse,
        Map<String, Object> metadata
) {
}
