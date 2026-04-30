package project.chat.application.event;

import project.chat.application.in.command.model.AcceptChatRequestResult;

public record ChatRequestAcceptedEvent(String requestId, Long senderId, AcceptChatRequestResult chatRoom) {
}
