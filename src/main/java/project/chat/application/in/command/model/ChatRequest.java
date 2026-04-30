package project.chat.application.in.command.model;

import java.time.LocalDateTime;

public record ChatRequest(
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
