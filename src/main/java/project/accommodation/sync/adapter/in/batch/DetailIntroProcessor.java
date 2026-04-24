package project.accommodation.sync.adapter.in.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.application.model.AccommodationProcessorDto;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;
import project.accommodation.sync.application.worker.DetailIntroWorker;

@Slf4j
@Component
@RequiredArgsConstructor
public class DetailIntroProcessor implements ItemProcessor<AccommodationProcessorDto, AccommodationProcessorDto> {

    private final HttpClientTemplate<TourApiClient> httpClientTemplate;

    @Override
    public AccommodationProcessorDto process(AccommodationProcessorDto dto) {
        DetailIntroWorker worker = new DetailIntroWorker(httpClientTemplate, dto);
        worker.run();

        return dto;
    }
}
