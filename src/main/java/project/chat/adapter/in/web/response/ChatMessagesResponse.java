package project.chat.adapter.in.web.response;

import project.chat.adapter.in.websocket.response.ChatMessageResponse;

import java.util.List;

public record ChatMessagesResponse(
        List<ChatMessageResponse> messages,
        boolean hasMore) {
}
