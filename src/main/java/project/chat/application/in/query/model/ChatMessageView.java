package project.chat.application.in.query.model;

import java.time.LocalDateTime;

public record ChatMessageView(
        String messageId,
        Long roomId,
        Long senderId,
        String senderName,
        String content,
        LocalDateTime timestamp,
        boolean left
) {
}
