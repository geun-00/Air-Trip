package project.chat.application.event;

import project.chat.adapter.in.web.response.ChatRoomResponse;

public record ChatRequestAcceptedEvent(String requestId, Long senderId, ChatRoomResponse chatRoomResponse) {
}
