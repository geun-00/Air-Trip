package project.chat.application.in.command.model;

import java.time.LocalDateTime;

public record UpdateChatRoomNameResult(
        Long roomId,
        String customRoomName,
        Long memberId,
        String memberName,
        String memberProfileImage,
        boolean otherMemberActive,
        String lastMessage,
        LocalDateTime lastMessageTime,
        int unreadCount
) {
}
