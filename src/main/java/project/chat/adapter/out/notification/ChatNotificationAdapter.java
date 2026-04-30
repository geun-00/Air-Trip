package project.chat.adapter.out.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import project.chat.adapter.out.notification.model.ChatRequestAcceptedNotification;
import project.chat.adapter.out.notification.model.ChatRequestNotification;
import project.chat.adapter.out.notification.model.ChatRequestRejectedNotification;
import project.chat.adapter.out.notification.model.ChatRoomLeftNotification;
import project.chat.adapter.out.notification.model.ChatRoomNotification;
import project.chat.application.in.command.model.AcceptChatRequestResult;
import project.chat.application.in.command.model.ChatRequest;
import project.chat.application.out.command.ChatNotificationPort;
import project.notification.application.service.NotificationService;
import project.notification.domain.NotificationType;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ChatNotificationAdapter implements ChatNotificationPort {

    private final NotificationService notificationService;
    private final SimpMessageSendingOperations messageTemplate;

    @Override
    public void sendChatRequest(ChatRequest chatRequest) {
        ChatRequestNotification notification = new ChatRequestNotification(
                chatRequest.requestId(),
                chatRequest.senderId(),
                chatRequest.senderName(),
                chatRequest.senderProfileImage(),
                chatRequest.expiresAt()
        );
        messageTemplate.convertAndSendToUser(
                String.valueOf(chatRequest.receiverId()),
                "/queue/chat-requests",
                notification
        );
        notificationService.createAndSendNotification(
                chatRequest.receiverId(),
                NotificationType.CHAT_REQUEST,
                "새로운 채팅 요청",
                chatRequest.senderName() + "님이 채팅을 요청했습니다.",
                chatRequest.requestId()
        );
    }

    @Override
    public void sendChatRequestAccepted(String requestId, Long senderId, AcceptChatRequestResult result) {
        String message = result.memberName() + "님이 채팅 요청을 수락했습니다.";

        ChatRequestAcceptedNotification notification = new ChatRequestAcceptedNotification(
                requestId,
                result.roomId(),
                message,
                toModel(result)
        );
        messageTemplate.convertAndSendToUser(
                String.valueOf(senderId),
                "/queue/chat-request-responses",
                notification
        );
        notificationService.createAndSendNotification(
                senderId,
                NotificationType.CHAT_REQUEST_ACCEPTED,
                "채팅 요청 수락",
                message,
                String.valueOf(result.roomId())
        );
    }

    private ChatRoomNotification toModel(AcceptChatRequestResult result) {
        return new ChatRoomNotification(
                result.roomId(),
                result.customRoomName(),
                result.memberId(),
                result.memberName(),
                result.memberProfileImage(),
                result.otherMemberActive(),
                result.lastMessage(),
                result.lastMessageTime(),
                result.unreadCount()
        );
    }

    @Override
    public void sendChatRequestRejected(String requestId, Long senderId, String receiverName) {
        String message = receiverName + "님이 채팅 요청을 거절했습니다.";

        ChatRequestRejectedNotification notification = new ChatRequestRejectedNotification(requestId, message);
        messageTemplate.convertAndSendToUser(
                String.valueOf(senderId),
                "/queue/chat-request-responses",
                notification
        );
        notificationService.createAndSendNotification(
                senderId,
                NotificationType.CHAT_REQUEST_REJECTED,
                "채팅 요청 거절",
                message,
                requestId
        );
    }

    @Override
    public void sendChatRoomLeft(String memberName, Long roomId) {
        ChatRoomLeftNotification leaveMessage = new ChatRoomLeftNotification(
                roomId,
                memberName + "님이 대화를 떠났습니다.",
                LocalDateTime.now()
        );
        messageTemplate.convertAndSend("/topic/" + roomId, leaveMessage);
    }
}
