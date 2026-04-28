package project.accommodation.sync.adapter.in.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.sync.application.model.AccommodationSyncDraft;
import project.accommodation.sync.application.writer.AccommodationSyncWriter;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccommodationWriter implements ItemWriter<AccommodationSyncDraft> {

    private final AccommodationSyncWriter accommodationSyncWriter;

    @Override
    @Transactional
    public void write(Chunk<? extends AccommodationSyncDraft> chunk) {
        accommodationSyncWriter.write(chunk.getItems());
    }
}
