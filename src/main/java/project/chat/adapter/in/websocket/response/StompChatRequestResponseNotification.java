package project.chat.adapter.in.websocket.response;

import lombok.Builder;
import project.chat.adapter.in.web.response.ChatRoomResponse;

@Builder
public record StompChatRequestResponseNotification(
        String requestId,
        boolean accepted,
        Long roomId,
        String message,
        ChatRoomResponse chatRoom
) {}