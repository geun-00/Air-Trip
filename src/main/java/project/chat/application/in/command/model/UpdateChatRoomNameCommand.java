package project.chat.application.in.command.model;

public record UpdateChatRoomNameCommand(
        Long roomId,
        Long memberId,
        Long otherMemberId,
        String roomName
) {
}
