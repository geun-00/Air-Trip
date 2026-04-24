package project.chat.application.event;

public record ChatRequestRejectedEvent(String requestId, Long senderId, String receiverName) {
}
