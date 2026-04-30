package project.chat.adapter.out.notification.model;

import java.time.LocalDateTime;

public record ChatRoomNotification(
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
