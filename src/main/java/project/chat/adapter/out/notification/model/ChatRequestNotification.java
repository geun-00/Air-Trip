package project.chat.adapter.out.notification.model;

import java.time.LocalDateTime;

public record ChatRequestNotification(
        String requestId,
        Long senderId,
        String senderName,
        String senderProfileImage,
        LocalDateTime expiresAt
) {
}
