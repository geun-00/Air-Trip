package project.chatbot.application.in.command.model;

public record AskChatbotCommand(
        Long memberId,
        String conversationId,
        String message
) {
}
