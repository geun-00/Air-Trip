package project.accommodation.sync.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.sync.application.fetcher.AreaCodeFetcher;
import project.accommodation.sync.application.fetcher.AreaListFetcher;
import project.accommodation.sync.application.fetcher.ChildAreaCodeFetcher;
import project.accommodation.sync.application.fetcher.DetailCommonFetcher;
import project.accommodation.sync.application.fetcher.DetailImageFetcher;
import project.accommodation.sync.application.fetcher.DetailInfoFetcher;
import project.accommodation.sync.application.fetcher.DetailIntroFetcher;
import project.accommodation.sync.application.model.AccommodationSyncDraft;
import project.accommodation.sync.application.model.AccommodationSyncSeed;
import project.accommodation.sync.application.model.AreaCodeSyncPayload;
import project.accommodation.sync.application.writer.AccommodationSyncWriter;
import project.accommodation.sync.application.writer.AreaCodeSyncWriter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourService {

    private final AreaCodeFetcher areaCodeFetcher;
    private final ChildAreaCodeFetcher childAreaCodeFetcher;
    private final AreaCodeSyncWriter areaCodeSyncWriter;

    private final AreaListFetcher areaListFetcher;
    private final DetailCommonFetcher detailCommonFetcher;
    private final DetailIntroFetcher detailIntroFetcher;
    private final DetailInfoFetcher detailInfoFetcher;
    private final DetailImageFetcher detailImageFetcher;
    private final AccommodationSyncWriter accommodationSyncWriter;

    @Transactional
    public void syncAreaCodes() {
        List<AreaCodeSyncPayload> areaCodes = areaCodeFetcher.fetch();

        for (AreaCodeSyncPayload areaCode : areaCodes) {
            areaCodeSyncWriter.write(areaCode, childAreaCodeFetcher.fetch(areaCode.code()));
        }
    }

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

        // TODO: 각 fetch 비동기 호출로 전환 가능
        draft.setCommon(detailCommonFetcher.fetch(contentId));
        draft.setIntro(detailIntroFetcher.fetch(contentId));
        draft.setInfo(detailInfoFetcher.fetch(contentId));
        draft.setImage(detailImageFetcher.fetch(contentId));
    }

    private void saveAccommodations(List<AccommodationSyncDraft> drafts) {
        accommodationSyncWriter.write(drafts);
    }
}
