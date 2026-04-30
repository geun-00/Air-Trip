package project.chat.adapter.in.web.response;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        Long roomId,
        String customRoomName,
        Long memberId,
        String memberName,
        String memberProfileImage,
        boolean isOtherMemberActive,
        String lastMessage,
        LocalDateTime lastMessageTime,
        int unreadCount
) {
}
