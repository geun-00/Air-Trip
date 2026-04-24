package project.notification.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.notification.adapter.in.web.response.NotificationResponse;
import project.notification.adapter.in.web.response.UnreadCountResponse;
import project.notification.domain.NotificationType;
import project.notification.application.service.NotificationService;

import java.util.List;

/**
 * 알림 CRUD 및 관리 API 컨트롤러
 * <p>
 * 알림 목록 조회, 읽음 처리, 삭제 등의 REST API를 제공합니다.
 * 실시간 알림 스트리밍(SSE)은 {@link SseController}를 참조하세요.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림 목록 조회
     *
     * @param isRead null: 전체, true: 읽은 알림, false: 읽지 않은 알림
     * @param type   null: 전체 타입, 지정 시 해당 타입만 조회
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(@CurrentMemberId Long memberId,
                                                                       @RequestParam(required = false) Boolean isRead,
                                                                       @RequestParam(required = false) NotificationType type) {
        List<NotificationResponse> notifications = notificationService.getNotifications(memberId, isRead, type);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 미확인 알림 개수 조회
     */
    @GetMapping("/unread/count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(@CurrentMemberId Long memberId) {
        UnreadCountResponse count = notificationService.getUnreadCount(memberId);
        return ResponseEntity.ok(count);
    }

    /**
     * 특정 알림 읽음 처리
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId,
                                           @CurrentMemberId Long memberId) {
        notificationService.markAsRead(notificationId, memberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 전체 알림 읽음 처리
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@CurrentMemberId Long memberId) {
        notificationService.markAllAsRead(memberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 알림 삭제
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId,
                                                   @CurrentMemberId Long memberId) {
        notificationService.deleteNotification(notificationId, memberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 전체 알림 삭제
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAllNotifications(@CurrentMemberId Long memberId) {
        notificationService.deleteAllNotifications(memberId);
        return ResponseEntity.ok().build();
    }
}
