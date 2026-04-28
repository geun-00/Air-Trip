package project.history.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;
import project.history.application.in.query.model.RecentViewHistoryView;
import project.history.application.out.command.SaveViewHistoryPort;
import project.history.application.out.query.LoadRecentViewHistoryPort;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ViewHistoryRedisAdapter implements SaveViewHistoryPort, LoadRecentViewHistoryPort {

    private static final String KEY_PREFIX = "member:history:";
    private static final int MAX_HISTORY_SIZE = 50;
    private static final long EXPIRE_DAYS = 30;

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(Long memberId, Long accommodationId) {
        String key = generateKey(memberId);
        double now = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(key, accommodationId.toString(), now);
        removeExpired(key, now);
        trim(key);
        redisTemplate.expire(key, Duration.ofDays(EXPIRE_DAYS));
    }

    private void removeExpired(String key, double now) {
        long expiredBefore = (long) now - Duration.ofDays(EXPIRE_DAYS).toMillis();
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, expiredBefore);
    }

    private void trim(String key) {
        Long size = redisTemplate.opsForZSet().zCard(key);
        if (size != null && size > MAX_HISTORY_SIZE) {
            redisTemplate.opsForZSet().removeRange(key, 0, size - MAX_HISTORY_SIZE - 1);
        }
    }

    @Override
    public List<RecentViewHistoryView> loadRecentViewHistories(Long memberId) {
        String key = generateKey(memberId);
        Set<TypedTuple<String>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);

        if (tuples == null || tuples.isEmpty()) {
            return Collections.emptyList();
        }

        return tuples.stream()
                     .map(tuple -> new RecentViewHistoryView(
                             Long.valueOf(Objects.requireNonNull(tuple.getValue())),
                             LocalDateTime.ofInstant(
                                     Instant.ofEpochMilli(Objects.requireNonNull(tuple.getScore()).longValue()),
                                     ZoneId.systemDefault()
                             )
                     ))
                     .toList();
    }

    private String generateKey(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}
