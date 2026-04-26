package project.accommodation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.in.query.GetRecentViewAccommodationsQueryUseCase;
import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.accommodation.application.in.query.model.ViewHistoryAccommodationView;
import project.accommodation.application.in.query.model.ViewHistoryGroupView;
import project.accommodation.application.out.query.LoadAccommodationCommonInfoPort;
import project.accommodation.application.out.query.LoadAccommodationWishlistPort;
import project.accommodation.application.out.query.model.WishlistInfoView;
import project.accommodation.application.service.model.AccommodationWishlistState;
import project.history.application.in.query.GetRecentViewHistoryUseCase;
import project.history.application.in.query.model.RecentViewHistoryView;

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

    private final GetRecentViewHistoryUseCase getRecentViewHistoryUseCase;
    private final LoadAccommodationWishlistPort loadAccommodationWishlistPort;
    private final LoadAccommodationCommonInfoPort loadAccommodationCommonInfoPort;

    @Override
    public List<ViewHistoryGroupView> getRecentViewAccommodations(Long memberId) {
        List<RecentViewHistoryView> recentViewHistories = getRecentViewHistoryUseCase.getRecentViewHistories(memberId);

        if (recentViewHistories.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> accommodationIds = extractIds(recentViewHistories);
        Map<Long, LocalDateTime> viewedAtMap = toViewedAtMap(recentViewHistories);
        Map<Long, WishlistInfoView> wishlistMap = loadAccommodationWishlistPort.loadWishlistInfos(accommodationIds, memberId);

        List<ViewHistoryAccommodationView> recentViewAccommodations = accommodationIds.stream()
                                                                                      .map(accommodationId -> toRecentViewAccommodation(
                                                                                              accommodationId,
                                                                                              viewedAtMap.get(accommodationId),
                                                                                              wishlistMap.get(accommodationId)
                                                                                      ))
                                                                                      .toList();
        return groupByViewedDate(recentViewAccommodations);
    }

    private List<Long> extractIds(List<RecentViewHistoryView> recentViewHistories) {
        return recentViewHistories.stream()
                                  .map(RecentViewHistoryView::accommodationId)
                                  .toList();
    }

    private Map<Long, LocalDateTime> toViewedAtMap(List<RecentViewHistoryView> recentViewHistories) {
        return recentViewHistories.stream()
                                  .collect(toMap(
                                          RecentViewHistoryView::accommodationId,
                                          RecentViewHistoryView::viewedAt,
                                          (first, second) -> first,
                                          LinkedHashMap::new
                                  ));
    }

    private ViewHistoryAccommodationView toRecentViewAccommodation(
            Long accommodationId,
            LocalDateTime viewedAt, WishlistInfoView wishlistInfoView
    ) {
        AccommodationCommonInfoView commonInfo = loadAccommodationCommonInfoPort.loadAccommodationCommonInfo(accommodationId);
        AccommodationWishlistState wishlistState = AccommodationWishlistState.from(wishlistInfoView);

        return new ViewHistoryAccommodationView(
                viewedAt,
                accommodationId,
                commonInfo.getTitle(),
                commonInfo.getAvgRate(),
                commonInfo.getImages().thumbnail(),
                wishlistState.isInWishlist(),
                wishlistState.wishlistId(),
                wishlistState.wishlistName()
        );
    }

    private List<ViewHistoryGroupView> groupByViewedDate(List<ViewHistoryAccommodationView> recentViewAccommodations) {
        return recentViewAccommodations.stream()
                                       .collect(groupingBy(
                                               accommodation -> accommodation.viewDate().toLocalDate(),
                                               LinkedHashMap::new,
                                               toList()
                                       ))
                                       .entrySet()
                                       .stream()
                                       .map(entry -> new ViewHistoryGroupView(entry.getKey(), entry.getValue()))
                                       .toList();
    }
}
