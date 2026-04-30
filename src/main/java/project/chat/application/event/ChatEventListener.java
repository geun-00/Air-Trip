package project.chat.application.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import project.chat.application.out.command.ChatNotificationPort;

@Component
@RequiredArgsConstructor
public class ChatEventListener {

    private final ChatNotificationPort chatNotificationPort;

    @EventListener
    public void handleChatRequestCreatedEvent(ChatRequestCreatedEvent event) {
        chatNotificationPort.sendChatRequest(event.chatRequest());
    }

    @EventListener
    public void handleChatRequestAcceptedEvent(ChatRequestAcceptedEvent event) {
        chatNotificationPort.sendChatRequestAccepted(event.requestId(), event.senderId(), event.chatRoom());
    }

    @EventListener
    public void handleChatRequestRejectedEvent(ChatRequestRejectedEvent event) {
        chatNotificationPort.sendChatRequestRejected(event.requestId(), event.senderId(), event.receiverName());
    }

    @TransactionalEventListener
    public void handleChatLeaveEvent(ChatLeaveEvent event) {
        chatNotificationPort.sendChatRoomLeft(event.name(), event.roomId());
    }
}
