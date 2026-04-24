package project.accommodation.sync.application.worker;

import lombok.extern.slf4j.Slf4j;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.application.model.AccommodationProcessorDto;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;

import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
public record DetailImageWorker(HttpClientTemplate<TourApiClient> httpClientTemplate,
                                AccommodationProcessorDto dto) implements Runnable {

    @Override
    public void run() {
        String contentId = dto.getContentId();

        List<Map<String, String>> items = httpClientTemplate.fetchItems(
                client -> client.detailImage(contentId),
                itemList -> {
                    if (itemList.size() > 10) {
                        log.info("detailImage 10개 이상, contentId: {}", dto.getContentId());
                    }
                });

        if (items.isEmpty()) {
            return;
        }

        for (Map<String, String> item : items) {
            String url = item.get("originimgurl");

            if (hasText(url)) {
                dto.addOriginImgUrl(url);
            }
        }
    }
}
