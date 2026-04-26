package project.accommodation.adapter.out.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.accommodation.application.out.command.EvictAccommodationCommonInfoPort;
import project.accommodation.application.out.query.LoadAccommodationCommonInfoPort;
import project.accommodation.application.out.query.LoadAccommodationCommonInfoSourcePort;
import project.infrastructure.time.StayDatePolicyProvider;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AccommodationCommonInfoCacheAdapter implements LoadAccommodationCommonInfoPort,
                                                            EvictAccommodationCommonInfoPort {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StayDatePolicyProvider stayDatePolicyProvider;
    private final LoadAccommodationCommonInfoSourcePort loadAccommodationCommonInfoSourcePort;

    @Override
    public AccommodationCommonInfoView loadAccommodationCommonInfo(Long accommodationId) {
        String key = buildKey(accommodationId);

        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof AccommodationCommonInfoView commonInfo) {
            return commonInfo;
        }

        AccommodationCommonInfoView commonInfo = loadAccommodationCommonInfoSourcePort.loadAccommodationCommonInfo(
                accommodationId,
                stayDatePolicyProvider.todayStayDatePolicy()
        );

        long ttlMs = generateTtlMs();
        redisTemplate.opsForValue().set(key, commonInfo, ttlMs, TimeUnit.MILLISECONDS);

        return commonInfo;
    }

    @Override
    public void evictAccommodationCommonInfo(Long accommodationId) {
        redisTemplate.delete(buildKey(accommodationId));
    }

    private String buildKey(Long accommodationId) {
        return "accommodation:commonInfo:" + accommodationId;
    }

    private long generateTtlMs() {
        long baseTtlMs = Duration.ofHours(1).toMillis();
        long jitterRange = Duration.ofMinutes(1).toMillis() + 1;

        return baseTtlMs + ThreadLocalRandom.current().nextLong(jitterRange);
    }
}
