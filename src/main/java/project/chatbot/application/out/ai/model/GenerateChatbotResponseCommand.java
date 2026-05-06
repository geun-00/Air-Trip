package project.chatbot.application.out.ai.model;

public record GenerateChatbotResponseCommand(
        boolean login,
        String conversationId,
        String message
) {
}
