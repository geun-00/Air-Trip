package project.chat.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import project.chat.adapter.in.websocket.response.ChatMessageResponse;
import project.chat.adapter.in.web.response.ChatRoomResponse;
import project.chat.adapter.in.websocket.response.StompChatRequestNotification;
import project.chat.adapter.in.websocket.response.StompChatRequestResponseNotification;
import project.notification.domain.NotificationType;
import project.chat.adapter.out.redis.model.ChatRequest;
import project.notification.application.service.NotificationService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatNotifyService {

    private final SimpMessageSendingOperations messageTemplate;
    private final NotificationService notificationService;

    public void sendChatRequestNotification(ChatRequest chatRequest) {
        // 1. 기존 WebSocket 알림 (채팅 화면용)
        StompChatRequestNotification notification = StompChatRequestNotification.builder()
                                                                                .requestId(chatRequest.getRequestId())
                                                                                .senderId(chatRequest.getSenderId())
                                                                                .senderName(chatRequest.getSenderName())
                                                                                .senderProfileImage(chatRequest.getSenderProfileImage())
                                                                                .expiresAt(chatRequest.getExpiresAt())
                                                                                .build();
        messageTemplate.convertAndSendToUser(
                String.valueOf(chatRequest.getReceiverId()),
                "/queue/chat-requests",
                notification);

        // 2. 통합 알림 시스템 (모든 화면용)
        notificationService.createAndSendNotification(
                chatRequest.getReceiverId(),
                NotificationType.CHAT_REQUEST,
                "새로운 채팅 요청",
                chatRequest.getSenderName() + "님이 채팅을 요청했습니다.",
                chatRequest.getRequestId()
        );
    }

    public void sendChatRequestAcceptedNotification(String requestId, Long senderId, ChatRoomResponse chatRoomResponse) {
        String message = chatRoomResponse.memberName() + "님이 채팅 요청을 수락했습니다.";

        // 1. 기존 WebSocket 알림
        StompChatRequestResponseNotification notification = StompChatRequestResponseNotification.builder()
                                                                                                .requestId(requestId)
                                                                                                .accepted(true)
                                                                                                .message(message)
                                                                                                .roomId(chatRoomResponse.roomId())
                                                                                                .chatRoom(chatRoomResponse)
                                                                                                .build();
        messageTemplate.convertAndSendToUser(
                String.valueOf(senderId),
                "/queue/chat-request-responses",
                notification
        );

        // 2. 통합 알림 시스템
        notificationService.createAndSendNotification(
                senderId,
                NotificationType.CHAT_REQUEST_ACCEPTED,
                "채팅 요청 수락",
                message,
                String.valueOf(chatRoomResponse.roomId())
        );
    }

    public void sendChatRequestRejectedNotification(String requestId, Long senderId, String receiverName) {
        String message = receiverName + "님이 채팅 요청을 거절했습니다.";
        // 1. 기존 WebSocket 알림
        StompChatRequestResponseNotification notification = StompChatRequestResponseNotification.builder()
                                                                                                .requestId(requestId)
                                                                                                .accepted(false)
                                                                                                .message(message)
                                                                                                .roomId(null)
                                                                                                .chatRoom(null)
                                                                                                .build();
        messageTemplate.convertAndSendToUser(
                String.valueOf(senderId),
                "/queue/chat-request-responses",
                notification
        );

        // 2. 통합 알림 시스템
        notificationService.createAndSendNotification(
                senderId,
                NotificationType.CHAT_REQUEST_REJECTED,
                "채팅 요청 거절",
                message,
                requestId
        );
    }

    public void sendChatLeaveNotification(String name, Long roomId) {
        ChatMessageResponse leaveMessage = ChatMessageResponse.builder()
                                                              .messageId(null)
                                                              .roomId(roomId)
                                                              .senderId(null)
                                                              .senderName(null)
                                                              .content(name + "님이 대화를 떠났습니다.")
                                                              .timestamp(LocalDateTime.now())
                                                              .left(true)
                                                              .build();

        messageTemplate.convertAndSend("/topic/" + roomId, leaveMessage);
    }
}
