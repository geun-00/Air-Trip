package project.member.application.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.accommodation.application.out.query.LoadAccommodationCommonInfoPort;
import project.accommodation.application.out.query.LoadAccommodationWishlistPort;
import project.accommodation.application.out.query.model.WishlistInfoView;
import project.accommodation.application.service.model.AccommodationWishlistState;
import project.history.application.in.query.GetRecentViewHistoryUseCase;
import project.history.application.in.query.model.RecentViewHistoryView;
import project.member.application.in.query.GetRecentViewAccommodationsQueryUseCase;
import project.member.application.in.query.model.ViewHistoryAccommodationView;
import project.member.application.in.query.model.ViewHistoryGroupView;

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
        Map<Long, AccommodationCommonInfoView> commonInfoMap = loadAccommodationCommonInfoPort.loadAccommodationCommonInfos(accommodationIds);

        List<ViewHistoryAccommodationView> recentViewAccommodations = accommodationIds.stream()
                                                                                      .map(accommodationId -> {
                                                                                          LocalDateTime viewedAt = viewedAtMap.get(accommodationId);
                                                                                          AccommodationWishlistState wishlistState = AccommodationWishlistState.from(wishlistMap.get(accommodationId));
                                                                                          AccommodationCommonInfoView commonInfo = commonInfoMap.get(accommodationId);

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
                                                                                      })
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
