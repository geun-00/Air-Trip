package project.chat.application.in.command.model;

public record RequestChatCommand(
        Long senderId,
        Long receiverId
) {
}
