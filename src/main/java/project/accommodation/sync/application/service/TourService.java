package project.accommodation.sync.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.application.worker.AccommodationSaveWorker;
import project.accommodation.sync.application.worker.AreaListWorker;
import project.accommodation.sync.application.worker.DetailCommonWorker;
import project.accommodation.sync.application.worker.DetailImageWorker;
import project.accommodation.sync.application.worker.DetailInfoWorker;
import project.accommodation.sync.application.worker.DetailIntroWorker;
import project.accommodation.sync.application.model.AccommodationProcessorDto;
import project.accommodation.sync.adapter.out.persistence.TourRepositoryFacadeManager;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourService {

    private final HttpClientTemplate<TourApiClient> httpClientTemplate;
    private final TourRepositoryFacadeManager tourRepositoryFacadeManager;

    @Transactional
    public void fetchAccommodations(int pageNo, int numOfRows) {
        AreaListWorker worker = new AreaListWorker(httpClientTemplate, tourRepositoryFacadeManager);
        List<AccommodationProcessorDto> dtoList = worker.run(pageNo, numOfRows);

        dtoList.forEach(this::fillDto);

        saveAccommodations(dtoList);
    }

    private void fillDto(AccommodationProcessorDto dto) {
        new DetailCommonWorker(httpClientTemplate, dto).run();
        new DetailIntroWorker(httpClientTemplate, dto).run();
        new DetailInfoWorker(httpClientTemplate, dto).run();
        new DetailImageWorker(httpClientTemplate, dto).run();
    }

    private void saveAccommodations(List<AccommodationProcessorDto> dtoList) {
        AccommodationSaveWorker worker = new AccommodationSaveWorker(
                tourRepositoryFacadeManager,
                dtoList,
                this::hasMandatoryFields
        );

        worker.run();
    }

    private boolean hasMandatoryFields(AccommodationProcessorDto dto) {
        return hasText(dto.getTitle()) && hasText(dto.getContentId()) && dto.getModifiedTime() != null && hasText(dto.getSigunguCode()) &&
                hasText(dto.getAddress()) && dto.getMapX() != null && dto.getMapY() != null &&
                dto.hasThumbnail() && dto.hasAllPrices();
    }
}
