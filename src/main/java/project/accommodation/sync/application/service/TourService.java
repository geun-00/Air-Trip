package project.accommodation.sync.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.sync.application.fetcher.AreaListFetcher;
import project.accommodation.sync.application.fetcher.DetailCommonFetcher;
import project.accommodation.sync.application.fetcher.DetailImageFetcher;
import project.accommodation.sync.application.fetcher.DetailInfoFetcher;
import project.accommodation.sync.application.fetcher.DetailIntroFetcher;
import project.accommodation.sync.application.model.AccommodationSyncDraft;
import project.accommodation.sync.application.model.AccommodationSyncSeed;
import project.accommodation.sync.application.writer.AccommodationSyncWriter;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourService {

    private final AreaListFetcher areaListFetcher;
    private final DetailCommonFetcher detailCommonFetcher;
    private final DetailIntroFetcher detailIntroFetcher;
    private final DetailInfoFetcher detailInfoFetcher;
    private final DetailImageFetcher detailImageFetcher;
    private final AccommodationSyncWriter accommodationSyncWriter;

    @Transactional
    public void fetchAccommodations(int pageNo, int numOfRows) {
        List<AccommodationSyncSeed> seeds = areaListFetcher.fetch(pageNo, numOfRows);

        List<AccommodationSyncDraft> drafts = seeds.stream()
                                                   .map(AccommodationSyncDraft::new)
                                                   .toList();
        drafts.forEach(this::fillDraft);
        saveAccommodations(drafts);
    }

    private void fillDraft(AccommodationSyncDraft draft) {
        String contentId = draft.getSeed().contentId();

        // TODO: 각 fetch는 독립적이므로 제한된 executor 기반 비동기 병렬 호출로 전환 가능
        draft.setCommon(detailCommonFetcher.fetch(contentId));
        draft.setIntro(detailIntroFetcher.fetch(contentId));
        draft.setInfo(detailInfoFetcher.fetch(contentId));
        draft.setImage(detailImageFetcher.fetch(contentId));
    }

    private void saveAccommodations(List<AccommodationSyncDraft> drafts) {
        accommodationSyncWriter.write(drafts);
    }
}
