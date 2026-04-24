package project.notification.adapter.in.web.response;

import lombok.Builder;
import project.notification.domain.Notification;
import project.notification.domain.NotificationType;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        Long notificationId,
        Long memberId,  // SSE 전송을 위해 추가
        NotificationType type,
        String title,
        String content,
        String referenceId,
        boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {
    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                                   .notificationId(notification.getId())
                                   .memberId(notification.getMember().getId())
                                   .type(notification.getType())
                                   .title(notification.getTitle())
                                   .content(notification.getContent())
                                   .referenceId(notification.getReferenceId())
                                   .isRead(notification.isRead())
                                   .createdAt(notification.getCreatedAt())
                                   .readAt(notification.getReadAt())
                                   .build();
    }
}
