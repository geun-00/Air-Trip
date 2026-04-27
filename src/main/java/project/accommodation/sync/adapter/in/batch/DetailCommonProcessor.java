package project.accommodation.sync.adapter.in.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import project.accommodation.sync.application.fetcher.DetailCommonFetcher;
import project.accommodation.sync.application.model.AccommodationSyncDraft;
import project.accommodation.sync.application.model.AccommodationSyncSeed;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Component
@RequiredArgsConstructor
public class DetailCommonProcessor implements ItemProcessor<AccommodationSyncSeed, AccommodationSyncDraft> {

    private final DetailCommonFetcher detailCommonFetcher;

    @Override
    public AccommodationSyncDraft process(AccommodationSyncSeed seed) {
        AccommodationSyncDraft draft = new AccommodationSyncDraft(seed);
        draft.setCommon(detailCommonFetcher.fetch(seed.contentId()));
        return hasMandatoryFields(draft) ? draft : null;
    }

    private boolean hasMandatoryFields(AccommodationSyncDraft draft) {
        return hasText(draft.getCommon().getTitle())
                && hasText(draft.getCommon().getAreaCode())
                && hasText(draft.getCommon().getAddress())
                && draft.getCommon().getMapX() != null
                && draft.getCommon().getMapY() != null
                && hasText(draft.getSeed().contentId())
                && draft.getSeed().modifiedTime() != null;
    }
}
