package project.notification.adapter.out;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import project.notification.adapter.in.web.response.NotificationResponse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE Emitter 관리 서비스
 * 사용자별 SSE 연결을 관리하고 알림을 전송합니다.
 */
@Slf4j
@Service
public class SseEmitterService {

    // 사용자 ID별 SSE Emitter 저장소
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE 연결 타임아웃 (30분)
    private static final Long DEFAULT_TIMEOUT = 30 * 60 * 1000L;

    /**
     * SSE 연결 생성 및 등록
     */
    public SseEmitter createEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        // 기존 연결이 있다면 제거
        if (emitters.containsKey(memberId)) {
            SseEmitter oldEmitter = emitters.get(memberId);
            oldEmitter.complete();
            log.info("기존 SSE 연결 종료 - 사용자: {}", memberId);
        }

        emitters.put(memberId, emitter);
        log.info("SSE 연결 생성 - 사용자: {}, 현재 연결 수: {}", memberId, emitters.size());

        // 연결 완료 시 처리
        emitter.onCompletion(() -> {
            emitters.remove(memberId);
            log.info("SSE 연결 완료 - 사용자: {}", memberId);
        });

        // 타임아웃 시 처리
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(memberId);
            log.warn("SSE 연결 타임아웃 - 사용자: {}", memberId);
        });

        // 에러 발생 시 처리
        emitter.onError(e -> {
            emitter.complete();
            emitters.remove(memberId);
            log.error("SSE 연결 오류 - 사용자: {}, 오류: {}", memberId, e.getMessage());
        });

        // 초기 연결 확인용 이벤트 전송 (연결 성공 확인)
        try {
            emitter.send(SseEmitter.event()
                                   .name("connect")
                                   .data("Connected to notification stream"));
            log.debug("SSE 연결 확인 이벤트 전송 - 사용자: {}", memberId);
        } catch (IOException e) {
            emitters.remove(memberId);
            log.error("SSE 초기 이벤트 전송 실패 - 사용자: {}", memberId, e);
        }

        return emitter;
    }

    /**
     * 특정 사용자에게 알림 전송
     */
    public void sendNotification(Long memberId, NotificationResponse notification) {
        SseEmitter emitter = emitters.get(memberId);

        if (emitter == null) {
            log.debug("SSE 전송 실패 (사용자 미연결) - 사용자: {}", memberId);
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                                   .name("notification")
                                   .data(notification));
            log.debug("SSE 알림 전송 성공 - 사용자: {}, 알림 ID: {}", memberId, notification.notificationId());
        } catch (IOException e) {
            // 전송 실패 시 emitter 제거
            emitters.remove(memberId);
            emitter.completeWithError(e);
            log.error("SSE 알림 전송 실패 - 사용자: {}, 오류: {}", memberId, e.getMessage());
        }
    }

    /**
     * 연결된 사용자 수 조회
     */
    public int getConnectedCount() {
        return emitters.size();
    }

    /**
     * 특정 사용자의 연결 여부 확인
     */
    public boolean isConnected(Long memberId) {
        return emitters.containsKey(memberId);
    }

    /**
     * 특정 사용자의 연결 종료
     */
    public void closeConnection(Long memberId) {
        SseEmitter emitter = emitters.remove(memberId);
        if (emitter != null) {
            emitter.complete();
            log.info("SSE 연결 수동 종료 - 사용자: {}", memberId);
        }
    }
}
