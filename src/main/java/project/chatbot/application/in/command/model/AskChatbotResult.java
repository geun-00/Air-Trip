package project.chatbot.application.in.command.model;

import java.util.Map;

public record AskChatbotResult(
        String textResponse,
        Map<String, Object> metadata
) {
}
