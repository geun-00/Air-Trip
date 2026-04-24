package project.accommodation.sync.application.worker;

import lombok.extern.slf4j.Slf4j;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.application.model.AccommodationProcessorDto;
import project.accommodation.domain.Accommodation;
import project.accommodation.sync.adapter.out.persistence.TourRepositoryFacadeManager;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public record AreaListWorker(HttpClientTemplate<TourApiClient> httpClientTemplate, TourRepositoryFacadeManager tourRepositoryFacadeManager) {

    public List<AccommodationProcessorDto> run(int pageNo, int numOfRows) {
        List<Map<String, String>> items = httpClientTemplate.fetchItems(client -> client.getAreaList(pageNo, numOfRows));

        List<String> contentIds = items.stream()
                                       .map(item -> item.get("contentid"))
                                       .toList();

        Map<String, Accommodation> existings = tourRepositoryFacadeManager.findByContentIdInToMap(contentIds);

        return items.stream()
                    .map(item ->
                            new AccommodationProcessorDto(
                                    item.get("contentid"),
                                    item.get("modifiedtime")
                            ))
                    .filter(dto -> {
                        Accommodation existing = existings.get(dto.getContentId());
                        if (existing == null) {
                            return true;
                        }
                        return dto.getModifiedTime().isAfter(existing.getModifiedTime());
                    })
                    .toList();
    }
}
