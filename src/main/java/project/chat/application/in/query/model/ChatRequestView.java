package project.chat.application.in.query.model;

import java.time.LocalDateTime;

public record ChatRequestView(
        String requestId,
        Long senderId,
        String senderName,
        String senderProfileImage,
        Long receiverId,
        String receiverName,
        String receiverProfileImage,
        LocalDateTime expiresAt
) {
}
