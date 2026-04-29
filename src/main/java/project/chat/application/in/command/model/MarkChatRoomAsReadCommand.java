package project.chat.application.in.command.model;

public record MarkChatRoomAsReadCommand(
        Long roomId,
        Long memberId
) {
}
