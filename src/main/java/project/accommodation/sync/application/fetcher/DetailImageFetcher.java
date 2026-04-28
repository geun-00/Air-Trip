package project.accommodation.sync.application.fetcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.application.model.AccommodationImagePayload;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Component
@RequiredArgsConstructor
public class DetailImageFetcher {

    private final HttpClientTemplate<TourApiClient> httpClientTemplate;

    public AccommodationImagePayload fetch(String contentId) {
        List<Map<String, String>> items = httpClientTemplate.fetchItems(
                client -> client.detailImage(contentId),
                itemList -> {
                    if (itemList.size() > 10) {
                        log.info("detailImage 10개 이상, contentId: {}", contentId);
                    }
                });

        if (items.isEmpty()) {
            return new AccommodationImagePayload();
        }

        return toPayload(items);
    }

    private AccommodationImagePayload toPayload(List<Map<String, String>> items) {
        AccommodationImagePayload payload = new AccommodationImagePayload();
        payload.setOriginImgUrls(extractOriginImageUrls(items));

        return payload;
    }

    private List<String> extractOriginImageUrls(List<Map<String, String>> items) {
        Set<String> originImageUrls = new LinkedHashSet<>();

        for (Map<String, String> item : items) {
            String url = item.get("originimgurl");
            if (hasText(url)) {
                originImageUrls.add(url);
            }
        }

        return originImageUrls.stream().toList();
    }
}
