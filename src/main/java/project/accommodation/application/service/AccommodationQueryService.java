package project.accommodation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.in.query.GetAccommodationDetailQueryUseCase;
import project.accommodation.application.in.query.GetAccommodationPriceQueryUseCase;
import project.accommodation.application.in.query.GetMainAccommodationsQueryUseCase;
import project.accommodation.application.in.query.GetRecentViewAccommodationsQueryUseCase;
import project.accommodation.application.in.query.SearchAccommodationsQueryUseCase;
import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.accommodation.application.in.query.model.AccommodationDetailView;
import project.accommodation.application.in.query.model.AccommodationPriceView;
import project.accommodation.application.in.query.model.AccommodationSearchQuery;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.in.query.model.MainAccommodationView;
import project.accommodation.application.in.query.model.ViewHistoryAccommodationView;
import project.accommodation.application.in.query.model.ViewHistoryGroupView;
import project.accommodation.application.out.query.GetAccommodationPricePort;
import project.accommodation.application.out.query.GetMainAccommodationsPort;
import project.accommodation.application.out.query.LoadAccommodationWishlistPort;
import project.accommodation.application.out.query.LoadReservedDatesPort;
import project.accommodation.application.out.query.MainAccommodationsCondition;
import project.accommodation.application.out.query.SearchAccommodationsCondition;
import project.accommodation.application.out.query.SearchAccommodationsPort;
import project.accommodation.application.out.query.model.ReservedDateView;
import project.accommodation.application.out.query.model.WishlistInfoView;
import project.common.adapter.in.web.response.PageResponse;
import project.common.domain.StayDatePolicy;
import project.history.application.event.ViewHistoryEvent;
import project.history.application.in.query.GetRecentViewHistoryUseCase;
import project.history.application.in.query.model.RecentViewHistoryView;
import project.infrastructure.cache.CacheService;
import project.infrastructure.time.DateManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationQueryService implements GetMainAccommodationsQueryUseCase,
                                                  SearchAccommodationsQueryUseCase,
                                                  GetAccommodationDetailQueryUseCase,
                                                  GetRecentViewAccommodationsQueryUseCase,
                                                  GetAccommodationPriceQueryUseCase {

    private final DateManager dateManager;
    private final CacheService cacheService;
    private final ApplicationEventPublisher eventPublisher;
    private final GetRecentViewHistoryUseCase getRecentViewHistoryUseCase;

    private final LoadReservedDatesPort loadReservedDatesPort;
    private final SearchAccommodationsPort searchAccommodationsPort;
    private final GetMainAccommodationsPort getMainAccommodationsPort;
    private final GetAccommodationPricePort getAccommodationPricePort;
    private final LoadAccommodationWishlistPort loadAccommodationWishlistPort;

    @Override
    public List<MainAccommodationView> getAccommodations(Long memberId) {
        StayDatePolicy stayDatePolicy = dateManager.getStayDatePolicy(LocalDate.now());
        MainAccommodationsCondition condition = new MainAccommodationsCondition(stayDatePolicy, memberId);

        return getMainAccommodationsPort.getAreaAccommodations(condition);
    }

    @Override
    public PageResponse<FilteredAccommodationView> getFilteredPagingAccommodations(
            AccommodationSearchQuery searchQuery,
            Long memberId
    ) {
        StayDatePolicy stayDatePolicy = dateManager.getStayDatePolicy(LocalDate.now());

        SearchAccommodationsCondition condition = new SearchAccommodationsCondition(
                searchQuery.areaCode(),
                searchQuery.amenities(),
                searchQuery.priceGoe(),
                searchQuery.priceLoe(),
                searchQuery.pageQuery(),
                memberId,
                stayDatePolicy
        );

        return searchAccommodationsPort.getFilteredPagingAccommodations(condition);
    }

    @Override
    public AccommodationDetailView getDetailAccommodation(Long accommodationId, Long memberId) {
        AccommodationCommonInfoView commonInfo = cacheService.getAccommodationCommonInfo(accommodationId);

        boolean isInWishlist = false;
        Long wishlistId = null;
        String wishlistName = null;
        List<ReservedDateView> reservedDates = loadReservedDatesPort.loadReservedDates(accommodationId);

        if (memberId != null) {
            eventPublisher.publishEvent(new ViewHistoryEvent(accommodationId, memberId));
            Optional<WishlistInfoView> wishlistInfo = loadAccommodationWishlistPort.loadWishlistInfo(accommodationId, memberId);
            if (wishlistInfo.isPresent()) {
                isInWishlist = wishlistInfo.get().isInWishlist();
                wishlistId = wishlistInfo.get().wishlistId();
                wishlistName = wishlistInfo.get().wishlistName();
            }
        }

        return new AccommodationDetailView(
                commonInfo.getAccommodationId(),
                commonInfo.getTitle(),
                commonInfo.getMaxPeople(),
                commonInfo.getAddress(),
                commonInfo.getMapX(),
                commonInfo.getMapY(),
                commonInfo.getCheckIn(),
                commonInfo.getCheckOut(),
                commonInfo.getDescription(),
                commonInfo.getNumber(),
                commonInfo.getRefundRegulation(),
                commonInfo.getPrice(),
                isInWishlist,
                wishlistId,
                wishlistName,
                commonInfo.getAvgRate(),
                commonInfo.getImages(),
                commonInfo.getAmenities(),
                commonInfo.getReviews(),
                reservedDates.stream()
                             .map(date -> new AccommodationDetailView.ReservedDateView(date.startDate(), date.endDate()))
                             .toList()
        );
    }

    @Override
    public AccommodationPriceView getAccommodationPrice(Long accommodationId, LocalDate date) {
        StayDatePolicy stayDatePolicy = dateManager.getStayDatePolicy(date);
        int price = getAccommodationPricePort.getAccommodationPrice(accommodationId, stayDatePolicy);

        return new AccommodationPriceView(accommodationId, date, price);
    }

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
                          .map(e -> new ViewHistoryGroupView(e.getKey(), e.getValue()))
                          .toList();
    }
}
