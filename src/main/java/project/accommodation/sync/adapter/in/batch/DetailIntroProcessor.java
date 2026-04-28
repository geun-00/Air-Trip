package project.accommodation.sync.adapter.in.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import project.accommodation.sync.application.fetcher.DetailIntroFetcher;
import project.accommodation.sync.application.model.AccommodationSyncDraft;

@Slf4j
@Component
@RequiredArgsConstructor
public class DetailIntroProcessor implements ItemProcessor<AccommodationSyncDraft, AccommodationSyncDraft> {

    private final DetailIntroFetcher detailIntroFetcher;

    @Override
    public AccommodationSyncDraft process(AccommodationSyncDraft draft) {
        draft.setIntro(detailIntroFetcher.fetch(draft.getSeed().contentId()));
        return draft;
    }
}
