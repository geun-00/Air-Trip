package project.chat.adapter.out.persistence.model;

import project.chat.domain.ChatMessageContent;
import project.chat.domain.ChatRoomName;

import java.time.LocalDateTime;

public record ChatRoomInfoRow(
        Long roomId,
        ChatRoomName customRoomName,
        Long memberId,
        String memberName,
        String memberProfileImage,
        boolean otherMemberActive,
        ChatMessageContent lastMessage,
        LocalDateTime lastMessageTime
) {
}
