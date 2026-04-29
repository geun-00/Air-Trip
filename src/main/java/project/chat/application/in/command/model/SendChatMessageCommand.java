package project.chat.application.in.command.model;

public record SendChatMessageCommand(Long roomId, Long senderId, String content) {
}
