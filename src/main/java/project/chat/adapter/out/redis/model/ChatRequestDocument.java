package project.chat.adapter.out.redis.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "chatRequest", timeToLive = 86400)
public class ChatRequestDocument {
    @Id
    private String requestId;

    @Indexed
    private Long senderId;
    private String senderName;
    private String senderProfileImage;

    @Indexed
    private Long receiverId;
    private String receiverName;
    private String receiverProfileImage;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
