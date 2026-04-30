package project.chat.application.out.query.model;

import java.time.LocalDateTime;

public record ChatMessageHistoryView(
        String messageId,
        Long roomId,
        Long senderId,
        String senderName,
        String content,
        LocalDateTime timestamp,
        boolean left
) {
}
