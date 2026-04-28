package project.history.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
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
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(Long memberId, Long accommodationId) {
        String key = generateKey(memberId);
        double now = System.currentTimeMillis();
        long expiredBefore = (long) now - Duration.ofDays(EXPIRE_DAYS).toMillis();

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            byte[] rawKey = redisTemplate.getStringSerializer().serialize(key);
            byte[] rawValue = redisTemplate.getStringSerializer().serialize(accommodationId.toString());

            connection.zSetCommands().zAdd(rawKey, now, rawValue);
            connection.zSetCommands().zRemRangeByScore(rawKey, 0, expiredBefore);
            connection.zSetCommands().zRemRange(rawKey, 0, -(MAX_HISTORY_SIZE + 1));
            connection.keyCommands().expire(rawKey, Duration.ofDays(EXPIRE_DAYS).toSeconds());

            return null;
        });
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
                                     ZONE_ID
                             )
                     ))
                     .toList();
    }

    private String generateKey(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}
