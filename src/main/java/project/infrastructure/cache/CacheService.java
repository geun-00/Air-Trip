package project.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import project.accommodation.adapter.out.persistence.AccommodationQueryRepository;
import project.accommodation.adapter.out.persistence.model.DetailAccommodationRow;
import project.accommodation.adapter.out.persistence.model.DetailReviewRow;
import project.accommodation.adapter.out.persistence.model.ImageDataRow;
import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.accommodation.domain.exception.AccommodationExceptions;
import project.common.domain.StayDatePolicy;
import project.infrastructure.time.StayDatePolicyProvider;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StayDatePolicyProvider stayDatePolicyProvider;
    private final AccommodationQueryRepository accommodationQueryRepository;

    public AccommodationCommonInfoView getAccommodationCommonInfo(Long accId) {
        String key = "accommodation:commonInfo:" + accId;

        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof AccommodationCommonInfoView commonInfo) {
            return commonInfo;
        }

        StayDatePolicy stayDatePolicy = stayDatePolicyProvider.todayStayDatePolicy();

        DetailAccommodationRow detail = accommodationQueryRepository.findAccommodation(accId, null, stayDatePolicy)
                                                                    .orElseThrow(() -> AccommodationExceptions.notFoundById(accId));
        List<String> amenities = accommodationQueryRepository.findAmenities(accId);
        List<DetailReviewRow> reviews = accommodationQueryRepository.findReviews(accId);
        List<ImageDataRow> images = accommodationQueryRepository.findImages(accId);

        AccommodationCommonInfoView result = AccommodationCommonInfoView.from(detail, amenities, reviews, images);

        long baseTtlMs = Duration.ofHours(1).toMillis();
        long jitterRange = Duration.ofMinutes(1).toMillis() + 1;

        long ttlMs = baseTtlMs + ThreadLocalRandom.current().nextLong(jitterRange);
        redisTemplate.opsForValue().set(key, result, ttlMs, TimeUnit.MILLISECONDS);

        return result;
    }

    public void evictAccCommonInfo(Long accId) {
        redisTemplate.delete("accommodation:commonInfo:" + accId);
    }
}
