package project.accommodation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.in.query.GetRecentViewAccommodationsQueryUseCase;
import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.accommodation.application.in.query.model.ViewHistoryAccommodationView;
import project.accommodation.application.in.query.model.ViewHistoryGroupView;
import project.accommodation.application.out.query.LoadAccommodationWishlistPort;
import project.history.application.in.query.GetRecentViewHistoryUseCase;
import project.history.application.in.query.model.RecentViewHistoryView;
import project.infrastructure.cache.CacheService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecentViewAccommodationQueryService implements GetRecentViewAccommodationsQueryUseCase {

    private final CacheService cacheService;
    private final GetRecentViewHistoryUseCase getRecentViewHistoryUseCase;
    private final LoadAccommodationWishlistPort loadAccommodationWishlistPort;

    @Override
    public List<ViewHistoryGroupView> getRecentViewAccommodations(Long memberId) {
        List<RecentViewHistoryView> recentViewHistories = getRecentViewHistoryUseCase.getRecentViewHistories(memberId);

        if (recentViewHistories.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, LocalDateTime> viewInfoMap = recentViewHistories.stream()
                                                                  .collect(toMap(
                                                                          RecentViewHistoryView::accommodationId,
                                                                          RecentViewHistoryView::viewedAt,
                                                                          (first, second) -> first,
                                                                          LinkedHashMap::new
                                                                  ));

        List<Long> accommodationIds = recentViewHistories.stream()
                                                         .map(RecentViewHistoryView::accommodationId)
                                                         .toList();
        var wishlistMap = loadAccommodationWishlistPort.loadWishlistInfos(accommodationIds, memberId);

        List<ViewHistoryAccommodationView> historyDtos = accommodationIds.stream()
                                                                         .map(id -> {
                                                                             AccommodationCommonInfoView commonInfo = cacheService.getAccommodationCommonInfo(id);
                                                                             var wishInfo = wishlistMap.getOrDefault(id, null);

                                                                             return new ViewHistoryAccommodationView(
                                                                                     viewInfoMap.get(id),
                                                                                     id,
                                                                                     commonInfo.getTitle(),
                                                                                     commonInfo.getAvgRate(),
                                                                                     commonInfo.getImages().thumbnail(),
                                                                                     wishInfo != null && wishInfo.isInWishlist(),
                                                                                     wishInfo == null ? null : wishInfo.wishlistId(),
                                                                                     wishInfo == null ? null : wishInfo.wishlistName()
                                                                             );
                                                                         })
                                                                         .toList();

        return historyDtos.stream()
                          .collect(groupingBy(dto -> dto.viewDate().toLocalDate(), LinkedHashMap::new, toList()))
                          .entrySet()
                          .stream()
                          .map(entry -> new ViewHistoryGroupView(entry.getKey(), entry.getValue()))
                          .toList();
    }
}
