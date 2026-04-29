package project.chat.application.in.command.model;

public record AcceptChatRequestCommand(
        String requestId,
        Long receiverId
) {
}
