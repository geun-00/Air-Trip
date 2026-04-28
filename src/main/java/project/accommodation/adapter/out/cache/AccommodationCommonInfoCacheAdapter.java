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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toMap;

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
        if (cached != null) {
            return (AccommodationCommonInfoView) cached;
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
    public Map<Long, AccommodationCommonInfoView> loadAccommodationCommonInfos(List<Long> accommodationIds) {
        List<String> keys = accommodationIds.stream()
                                            .map(this::buildKey)
                                            .toList();

        List<Object> cached = redisTemplate.opsForValue().multiGet(keys);

        Map<Boolean, List<Integer>> partitioned = IntStream.range(0, accommodationIds.size())
                                                           .boxed()
                                                           .collect(partitioningBy(i -> cached.get(i) != null));

        Map<Long, AccommodationCommonInfoView> result = partitioned.get(true).stream()
                                                                   .collect(toMap(
                                                                           accommodationIds::get,
                                                                           i -> (AccommodationCommonInfoView) cached.get(i)
                                                                   ));

        List<Long> missedIds = partitioned.get(false).stream()
                                          .map(accommodationIds::get)
                                          .toList();

        if (!missedIds.isEmpty()) {
            Map<Long, AccommodationCommonInfoView> fetched =
                    loadAccommodationCommonInfoSourcePort.loadAccommodationCommonInfos(
                            missedIds,
                            stayDatePolicyProvider.todayStayDatePolicy()
                    );

            fetched.forEach((id, commonInfo) -> {
                result.put(id, commonInfo);
                redisTemplate.opsForValue().set(buildKey(id), commonInfo, generateTtlMs(), TimeUnit.MILLISECONDS);
            });
        }

        return result;
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
