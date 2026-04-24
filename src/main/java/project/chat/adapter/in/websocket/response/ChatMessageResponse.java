package project.chat.adapter.in.websocket.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String messageId;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
    private boolean left;

    public ChatMessageResponse(Long messageId, Long roomId, Long senderId, String senderName, String content, LocalDateTime timestamp) {
        this.messageId = String.valueOf(messageId);
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
        this.left = false;
    }
}
