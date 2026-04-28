package project.accommodation.sync.adapter.in.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import project.accommodation.sync.application.fetcher.DetailInfoFetcher;
import project.accommodation.sync.application.model.AccommodationSyncDraft;

@Slf4j
@Component
@RequiredArgsConstructor
public class DetailInfoProcessor implements ItemProcessor<AccommodationSyncDraft, AccommodationSyncDraft> {

    private final DetailInfoFetcher detailInfoFetcher;

    @Override
    public AccommodationSyncDraft process(AccommodationSyncDraft draft) {
        draft.setInfo(detailInfoFetcher.fetch(draft.getSeed().contentId()));
        return draft.hasThumbnail() ? draft : null;
    }
}
