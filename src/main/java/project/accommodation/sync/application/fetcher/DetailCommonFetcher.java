package project.accommodation.sync.application.fetcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.application.model.AccommodationCommonPayload;

import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Component
@RequiredArgsConstructor
public class DetailCommonFetcher {

    private final HttpClientTemplate<TourApiClient> httpClientTemplate;

    public AccommodationCommonPayload fetch(String contentId) {
        List<Map<String, String>> items = httpClientTemplate.fetchItems(
                client -> client.detailCommon(contentId),
                itemList -> {
                    if (itemList.size() > 1) {
                        log.info("detailCommon 1개 이상, contentId: {}", contentId);
                    }
                });

        if (items.isEmpty()) {
            return new AccommodationCommonPayload();
        }

        return toPayload(items.getFirst());
    }

    private AccommodationCommonPayload toPayload(Map<String, String> item) {
        AccommodationCommonPayload payload = new AccommodationCommonPayload();

        String title = item.get("title");
        if (hasText(title)) {
            payload.setTitle(title);
        }

        String number = item.get("tel");
        if (hasText(number)) {
            payload.setNumber(number);
        }

        String thumbnailUrl = item.get("firstimage");
        if (hasText(thumbnailUrl)) {
            payload.setThumbnailUrl(thumbnailUrl);
        }

        String sigunguCode = toSigunguCode(item);
        if (hasText(sigunguCode)) {
            payload.setSigunguCode(sigunguCode);
        }

        String address = item.get("addr1");
        if (hasText(address)) {
            payload.setAddress(address);
        }

        String description = item.get("overview");
        if (hasText(description)) {
            payload.setDescription(description);
        }

        String mapX = item.get("mapx");
        if (hasText(mapX)) {
            payload.setMapX(Double.parseDouble(mapX));
        }

        String mapY = item.get("mapy");
        if (hasText(mapY)) {
            payload.setMapY(Double.parseDouble(mapY));
        }

        return payload;
    }

    private String toSigunguCode(Map<String, String> item) {
        String areaCode = item.get("areacode");
        String sigunguCode = item.get("sigungucode");

        if (!hasText(areaCode) || !hasText(sigunguCode)) {
            return null;
        }

        return areaCode + "-" + sigunguCode;
    }
}
