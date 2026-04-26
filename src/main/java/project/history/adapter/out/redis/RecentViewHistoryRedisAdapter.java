package project.history.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;
import project.history.application.in.query.model.RecentViewHistoryView;
import project.history.application.out.query.LoadRecentViewHistoryPort;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RecentViewHistoryRedisAdapter implements LoadRecentViewHistoryPort {

    private static final String KEY_PREFIX = "member:history:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public List<RecentViewHistoryView> loadRecentViewHistories(Long memberId) {
        String key = KEY_PREFIX + memberId;
        Set<TypedTuple<String>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);

        if (typedTuples == null || typedTuples.isEmpty()) {
            return Collections.emptyList();
        }

        return typedTuples.stream()
                          .map(tuple -> new RecentViewHistoryView(
                                  Long.valueOf(Objects.requireNonNull(tuple.getValue())),
                                  LocalDateTime.ofInstant(
                                          Instant.ofEpochMilli(Objects.requireNonNull(tuple.getScore()).longValue()),
                                          ZoneId.systemDefault()
                                  )
                          ))
                          .toList();
    }
}
