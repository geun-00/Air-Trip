package project.accommodation.sync.adapter.in.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import project.accommodation.sync.application.fetcher.DetailImageFetcher;
import project.accommodation.sync.application.model.AccommodationSyncDraft;

@Slf4j
@Component
@RequiredArgsConstructor
public class DetailImageProcessor implements ItemProcessor<AccommodationSyncDraft, AccommodationSyncDraft> {

    private final DetailImageFetcher detailImageFetcher;

    @Override
    public AccommodationSyncDraft process(AccommodationSyncDraft draft) {
        draft.setImage(detailImageFetcher.fetch(draft.getSeed().contentId()));
        return draft.hasThumbnail() ? draft : null;
    }
}
