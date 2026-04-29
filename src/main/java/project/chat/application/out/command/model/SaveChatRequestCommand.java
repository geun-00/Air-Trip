package project.chat.application.out.command.model;

import java.time.LocalDateTime;

public record SaveChatRequestCommand(
        Long senderId,
        String senderName,
        String senderProfileImage,
        Long receiverId,
        String receiverName,
        String receiverProfileImage,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}
