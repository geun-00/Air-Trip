package project.notification.adapter.in.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.notification.adapter.out.SseEmitterService;

/**
 * SSE(Server-Sent Events) 스트리밍 전용 컨트롤러
 * <p>
 * 실시간 알림 전송을 위한 SSE 연결을 관리합니다.
 * - 클라이언트가 이 엔드포인트에 연결하면 서버→클라이언트 단방향 실시간 통신이 가능합니다.
 * - EventSource API를 통해 자동 재연결을 지원합니다.
 * - 30분 타임아웃 후 자동으로 재연결됩니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterService sseEmitterService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@CurrentMemberId Long memberId) {
        log.info("SSE 구독 요청 - 회원 ID: {}", memberId);
        return sseEmitterService.createEmitter(memberId);
    }
}
