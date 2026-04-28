package project.history.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.domain.exception.AccommodationExceptions;
import project.member.domain.exception.MemberExceptions;
import project.accommodation.domain.Accommodation;
import project.history.domain.ViewHistory;
import project.member.domain.Member;
import project.accommodation.adapter.out.persistence.AccommodationRepository;
import project.member.adapter.out.persistence.MemberRepository;
import project.history.adapter.out.persistence.ViewHistoryRepository;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ViewHistoryService {

    private final MemberRepository memberRepository;
    private final ViewHistoryRepository viewHistoryRepository;
    private final AccommodationRepository accommodationRepository;

    private final StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "member:history:";
    private static final int MAX_HISTORY_COUNT = 50;
    private static final long EXPIRE_DAYS = 30;

    @Transactional
    public void saveRecentView(Long accommodationId, Long memberId) {
        int updated = viewHistoryRepository.updateViewedAt(accommodationId, memberId, LocalDateTime.now());

        if (updated == 0) {
            Accommodation accommodation = accommodationRepository.findById(accommodationId)
                                                                 .orElseThrow(() -> AccommodationExceptions.notFoundById(accommodationId));
            Member member = memberRepository.findById(memberId)
                                            .orElseThrow(() -> MemberExceptions.notFoundById(memberId));

            viewHistoryRepository.save(ViewHistory.ofNow(accommodation, member));
        }
    }

    public void addHistory(Long memberId, Long accommodationId) {
        String key = KEY_PREFIX + memberId;
        double now = (double) System.currentTimeMillis();

        updateRecentView(key, accommodationId, now);
        removeExpiredHistory(key, now);
        limitHistorySize(key);
        refreshKeyExpiration(key);
    }

    private void updateRecentView(String key, Long accommodationId, double score) {
        redisTemplate.opsForZSet().add(key, accommodationId.toString(), score);
    }

    private void removeExpiredHistory(String key, double now) {
        long thirtyDaysAgo = (long) now - Duration.ofDays(EXPIRE_DAYS).toMillis();
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, thirtyDaysAgo);
    }

    private void limitHistorySize(String key) {
        Long size = redisTemplate.opsForZSet().zCard(key);
        if (size != null && size > MAX_HISTORY_COUNT) {
            redisTemplate.opsForZSet().removeRange(key, 0, size - MAX_HISTORY_COUNT - 1);
        }
    }

    private void refreshKeyExpiration(String key) {
        redisTemplate.expire(key, Duration.ofDays(EXPIRE_DAYS));
    }
}
