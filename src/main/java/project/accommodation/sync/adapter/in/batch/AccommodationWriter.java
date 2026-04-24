package project.accommodation.sync.adapter.in.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.sync.application.model.AccommodationProcessorDto;
import project.accommodation.sync.adapter.out.persistence.TourRepositoryFacadeManager;
import project.accommodation.sync.application.worker.AccommodationSaveWorker;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccommodationWriter implements ItemWriter<AccommodationProcessorDto> {

    private final TourRepositoryFacadeManager tourRepositoryFacadeManager;

    @Override
    @Transactional
    public void write(Chunk<? extends AccommodationProcessorDto> chunk) throws Exception {
        AccommodationSaveWorker worker = new AccommodationSaveWorker(
                tourRepositoryFacadeManager,
                chunk.getItems(),
                dto -> true
                //배치에서는 이전 process 단계에서 이미 필수값 검증된 상태에서 넘어오므로 항상 true
        );
        worker.run();
    }
}
