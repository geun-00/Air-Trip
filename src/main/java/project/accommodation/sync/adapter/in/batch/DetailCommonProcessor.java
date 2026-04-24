package project.accommodation.sync.adapter.in.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.application.model.AccommodationProcessorDto;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;
import project.accommodation.sync.application.worker.DetailCommonWorker;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Component
@RequiredArgsConstructor
public class DetailCommonProcessor implements ItemProcessor<AccommodationProcessorDto, AccommodationProcessorDto> {

    private final HttpClientTemplate<TourApiClient> httpClientTemplate;

    @Override
    public AccommodationProcessorDto process(AccommodationProcessorDto dto) {
        DetailCommonWorker worker = new DetailCommonWorker(httpClientTemplate, dto);
        worker.run();

        return hasMandatoryFields(dto) ? dto : null;
    }

    private boolean hasMandatoryFields(AccommodationProcessorDto dto) {
        return hasText(dto.getTitle()) && hasText(dto.getSigunguCode()) &&
                hasText(dto.getAddress()) && dto.getMapX() != null && dto.getMapY() != null &&
                hasText(dto.getContentId()) && dto.getModifiedTime() != null;
    }
}
