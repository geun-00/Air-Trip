package project.chat.adapter.out.persistence.model;

import project.chat.domain.ChatMessageContent;
import project.member.domain.MemberName;

import java.time.LocalDateTime;

public record ChatMessageHistoryRow(
        Long messageId,
        Long roomId,
        Long senderId,
        MemberName senderName,
        ChatMessageContent content,
        LocalDateTime timestamp
) {
}
