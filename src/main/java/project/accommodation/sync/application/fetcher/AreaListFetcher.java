package project.accommodation.sync.application.fetcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.adapter.out.persistence.AccommodationSyncPersistenceAdapter;
import project.accommodation.sync.application.model.AccommodationSyncSeed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Component
@RequiredArgsConstructor
public class AreaListFetcher {

    private final HttpClientTemplate<TourApiClient> httpClientTemplate;
    private final AccommodationSyncPersistenceAdapter accommodationSyncPersistenceAdapter;

    public List<AccommodationSyncSeed> fetch(int pageNo, int numOfRows) {
        List<Map<String, String>> items = httpClientTemplate.fetchItems(client -> client.getAreaList(pageNo, numOfRows));

        List<AccommodationSyncSeed> seeds = items.stream()
                                                 .map(this::toSeedOrNull)
                                                 .filter(Objects::nonNull)
                                                 .toList();

        Map<String, LocalDateTime> existingModifiedTimes = accommodationSyncPersistenceAdapter.findModifiedTimesByContentIdIn(
                seeds.stream()
                     .map(AccommodationSyncSeed::contentId)
                     .toList()
        );

        return seeds.stream()
                    .filter(seed -> shouldSync(seed, existingModifiedTimes.get(seed.contentId())))
                    .toList();
    }

    private AccommodationSyncSeed toSeedOrNull(Map<String, String> item) {
        String contentId = item.get("contentid");
        String modifiedTime = item.get("modifiedtime");

        if (!hasText(contentId) || !hasText(modifiedTime)) {
            log.warn("숙소 목록 응답에 필수값이 없어 건너뜁니다. contentId={}, modifiedTime={}", contentId, modifiedTime);
            return null;
        }

        try {
            return AccommodationSyncSeed.of(contentId, modifiedTime);
        } catch (RuntimeException e) {
            log.warn("숙소 목록 응답 modifiedTime 파싱에 실패해 건너뜁니다. contentId={}, modifiedTime={}", contentId, modifiedTime);
            return null;
        }
    }

    private boolean shouldSync(
            AccommodationSyncSeed seed,
            LocalDateTime existingModifiedTime
    ) {
        return existingModifiedTime == null || seed.modifiedTime().isAfter(existingModifiedTime);
    }
}
