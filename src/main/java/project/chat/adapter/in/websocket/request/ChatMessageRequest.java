package project.chat.adapter.in.websocket.request;

public record ChatMessageRequest(
        Long senderId,
        String content) {
}
