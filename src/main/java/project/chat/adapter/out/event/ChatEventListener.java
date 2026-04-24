package project.chat.adapter.out.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import project.chat.application.service.ChatNotifyService;
import project.chat.application.event.ChatLeaveEvent;
import project.chat.application.event.ChatRequestAcceptedEvent;
import project.chat.application.event.ChatRequestCreatedEvent;
import project.chat.application.event.ChatRequestRejectedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatEventListener {

    private final ChatNotifyService chatNotifyService;

    @EventListener
    public void handleChatRequestCreatedEvent(ChatRequestCreatedEvent event) {
        chatNotifyService.sendChatRequestNotification(event.chatRequest());
    }

    @EventListener
    public void handleChatRequestAcceptedEvent(ChatRequestAcceptedEvent event) {
        chatNotifyService.sendChatRequestAcceptedNotification(event.requestId(), event.senderId(), event.chatRoomResponse());
    }

    @EventListener
    public void handleChatRequestRejectedEvent(ChatRequestRejectedEvent event) {
        chatNotifyService.sendChatRequestRejectedNotification(event.requestId(), event.senderId(), event.receiverName());
    }

    @TransactionalEventListener
    public void handleChatLeaveEvent(ChatLeaveEvent event) {
        chatNotifyService.sendChatLeaveNotification(event.name(), event.roomId());
    }
}
