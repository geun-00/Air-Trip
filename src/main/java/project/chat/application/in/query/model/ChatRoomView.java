package project.chat.application.in.query.model;

import java.time.LocalDateTime;

public record ChatRoomView(
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
