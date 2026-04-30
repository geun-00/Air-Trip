package project.chat.adapter.out.persistence.model;

import project.chat.domain.ChatMessageContent;

import java.time.LocalDateTime;

public record ChatMessageHistoryRow(
        Long messageId,
        Long roomId,
        Long senderId,
        String senderName,
        ChatMessageContent content,
        LocalDateTime timestamp
) {
}
