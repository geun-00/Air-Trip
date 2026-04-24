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
        int unreadCount) {

    public static ChatRoomResponse withUnreadCount(ChatRoomResponse dto, int unreadCount) {
        return new ChatRoomResponse(
                dto.roomId(), dto.customRoomName(), dto.memberId(),
                dto.memberName(), dto.memberProfileImage(), dto.isOtherMemberActive(),
                dto.lastMessage(), dto.lastMessageTime(), unreadCount
        );
    }
}
