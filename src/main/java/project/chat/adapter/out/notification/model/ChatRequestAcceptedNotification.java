package project.chat.adapter.out.notification.model;

public record ChatRequestAcceptedNotification(
        String requestId,
        Long roomId,
        String message,
        ChatRoomNotification chatRoom
) {
}
