package project.notification.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import project.notification.adapter.in.web.response.NotificationResponse;

@Slf4j
@Service
public class RedisNotificationSubscriber {

    private final ObjectMapper redisObjMapper;
    private final SseEmitterService sseEmitterService;

    public RedisNotificationSubscriber(@Qualifier("redisObjMapper") ObjectMapper redisObjMapper,
                                      SseEmitterService sseEmitterService) {
        this.redisObjMapper = redisObjMapper;
        this.sseEmitterService = sseEmitterService;
    }

    /**
     * Redis에서 발행된 알림을 SSE로 전송
     */
    public void handleNotification(String publishMessage) {
        try {
            NotificationResponse notification = redisObjMapper.readValue(publishMessage, NotificationResponse.class);
            
            log.info("알림 수신 - 알림 ID: {}, 사용자: {}, 타입: {}", 
                    notification.notificationId(), notification.memberId(), notification.type());
            
            // SSE로 실시간 전송
            sseEmitterService.sendNotification(notification.memberId(), notification);
            
        } catch (Exception e) {
            log.error("Redis 알림 처리 오류: {}", e.getMessage(), e);
        }
    }
}
