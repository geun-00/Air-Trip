package project.chat.adapter.out.notification.model;

public record ChatRequestRejectedNotification(
        String requestId,
        String message
) {
}
