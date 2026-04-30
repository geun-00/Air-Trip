package project.chat.adapter.out.notification.model;

import java.time.LocalDateTime;

public record ChatRoomLeftNotification(
        Long roomId,
        String message,
        LocalDateTime timestamp
) {
}
