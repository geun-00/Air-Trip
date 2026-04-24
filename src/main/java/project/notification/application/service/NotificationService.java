package project.notification.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.member.domain.exception.MemberExceptions;
import project.notification.adapter.in.web.response.NotificationResponse;
import project.notification.adapter.in.web.response.UnreadCountResponse;
import project.member.domain.Member;
import project.notification.domain.Notification;
import project.notification.domain.NotificationType;
import project.member.adapter.out.persistence.MemberRepository;
import project.notification.adapter.out.persistence.NotificationRepository;
import project.notification.adapter.out.persistence.NotificationQueryRepository;
import project.infrastructure.messaging.RedisMessagePublisher;
import project.notification.adapter.out.SseEmitterService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final ChannelTopic notificationTopic;
    private final MemberRepository memberRepository;
    private final RedisMessagePublisher redisMessagePublisher;
    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;
    private final SseEmitterService sseEmitterService;

    /**
     * 알림 생성 및 전송 (멀티 인스턴스 지원)
     */
    @Transactional
    public void createAndSendNotification(Long memberId, NotificationType type,
                                          String title, String content, String referenceId) {
        // 1. DB에 저장
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> MemberExceptions.notFoundById(memberId));

        Notification notification = Notification.builder()
                                                .member(member)
                                                .type(type)
                                                .title(title)
                                                .content(content)
                                                .referenceId(referenceId)
                                                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("알림 저장 완료 - ID: {}, 사용자: {}, 타입: {}",
                savedNotification.getId(), memberId, type);

        // 2. Redis Pub/Sub으로 발행 (모든 서버 인스턴스가 수신)
        NotificationResponse notificationDto = NotificationResponse.from(savedNotification);
        redisMessagePublisher.publish(notificationTopic.getTopic(), notificationDto);

        // 3. 현재 서버에 연결된 사용자에게도 직접 전송 (빠른 전달)
        sendToUser(memberId, notificationDto);

        log.info("알림 발행 완료 - Redis Topic: {}", notificationTopic.getTopic());
    }

    /**
     * 특정 사용자에게 SSE로 알림 전송
     */
    private void sendToUser(Long memberId, NotificationResponse notification) {
        try {
            sseEmitterService.sendNotification(memberId, notification);
            log.debug("SSE 알림 전송 - 사용자: {}", memberId);
        } catch (Exception e) {
            log.warn("SSE 전송 실패 (사용자 미연결): {}", memberId);
        }
    }

    /**
     * 알림 목록 조회 (읽음/읽지않음/타입 필터 지원)
     * QueryDSL을 사용한 동적 쿼리로 깔끔하게 처리
     * 
     * @param memberId 회원 ID
     * @param isRead 읽음 여부 (null이면 전체 조회)
     * @param type 알림 타입 (null이면 전체 타입)
     */
    public List<NotificationResponse> getNotifications(Long memberId, Boolean isRead, NotificationType type) {
        List<Notification> notifications = notificationQueryRepository.findNotifications(memberId, isRead, type);

        return notifications.stream()
                            .map(NotificationResponse::from)
                            .toList();
    }

    /**
     * 미확인 알림 개수 조회
     */
    public UnreadCountResponse getUnreadCount(Long memberId) {
        long count = notificationQueryRepository.countUnreadNotifications(memberId);
        return new UnreadCountResponse(count);
    }

    /**
     * 특정 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                                                          .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다: " + notificationId));

        if (!notification.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 알림만 읽을 수 있습니다.");
        }

        if (!notification.isRead()) {
            notification.markAsRead();
            log.info("알림 읽음 처리 - ID: {}, 사용자: {}", notificationId, memberId);
        }
    }

    /**
     * 전체 알림 읽음 처리
     */
    @Transactional
    public void markAllAsRead(Long memberId) {
        int count = notificationRepository.markAllAsReadByMemberId(memberId);
        log.info("전체 알림 읽음 처리 - 사용자: {}, 개수: {}", memberId, count);
    }

    /**
     * 특정 알림 삭제
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                                                          .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다: " + notificationId));

        if (!notification.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 알림만 삭제할 수 있습니다.");
        }

        notificationRepository.delete(notification);
        log.info("알림 삭제 - ID: {}, 사용자: {}", notificationId, memberId);
    }

    /**
     * 전체 알림 삭제
     */
    @Transactional
    public void deleteAllNotifications(Long memberId) {
        notificationRepository.deleteByMemberId(memberId);
        log.info("전체 알림 삭제 - 사용자: {}", memberId);
    }
}
