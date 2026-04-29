package project.chat.application.out.query.model;

import java.time.LocalDateTime;

public record ChatRoomInfoView(
        Long roomId,
        String customRoomName,
        Long memberId,
        String memberName,
        String memberProfileImage,
        boolean otherMemberActive,
        String lastMessage,
        LocalDateTime lastMessageTime
) {
}
