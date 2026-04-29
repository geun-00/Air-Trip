package project.chat.application.in.command.model;

public record RejectChatRequestCommand(String requestId, Long rejecterId) {
}
