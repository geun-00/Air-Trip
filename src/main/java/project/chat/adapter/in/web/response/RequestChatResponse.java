package project.chat.adapter.in.web.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RequestChatResponse(
        String requestId,
        Long senderId,
        String senderName,
        String senderProfileImage,
        Long receiverId,
        String receiverName,
        String receiverProfileImage,
        LocalDateTime expiresAt) {
}
