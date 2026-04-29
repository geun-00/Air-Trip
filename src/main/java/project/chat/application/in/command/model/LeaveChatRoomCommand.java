package project.chat.application.in.command.model;

public record LeaveChatRoomCommand(
        Long roomId,
        Long memberId
) {
}
