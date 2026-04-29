package project.chat.application.out.command.model;

import java.time.LocalDateTime;

public record ChatMessagePayload(
        String messageId,
        Long roomId,
        Long senderId,
        String senderName,
        String content,
        LocalDateTime timestamp,
        boolean left
) {
}
