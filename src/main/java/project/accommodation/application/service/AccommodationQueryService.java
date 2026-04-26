package project.accommodation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.adapter.in.web.request.AccommodationSearchCondition;
import project.accommodation.adapter.in.web.request.ViewHistoryDto;
import project.accommodation.adapter.in.web.response.AccommodationCommonInfo;
import project.accommodation.adapter.in.web.response.AccommodationPriceResDto;
import project.accommodation.adapter.in.web.response.DetailAccommodationResDto;
import project.accommodation.adapter.in.web.response.FilteredAccListResDto;
import project.accommodation.adapter.in.web.response.MainAccListResponse;
import project.accommodation.adapter.in.web.response.MainAccResDto;
import project.accommodation.adapter.in.web.response.ViewHistoryResDto;
import project.accommodation.application.in.query.GetAccommodationDetailQueryUseCase;
import project.accommodation.application.in.query.GetAccommodationPriceQueryUseCase;
import project.accommodation.application.in.query.GetMainAccommodationsQueryUseCase;
import project.accommodation.application.in.query.GetRecentViewAccommodationsQueryUseCase;
import project.accommodation.application.in.query.SearchAccommodationsQueryUseCase;
import project.history.application.event.ViewHistoryEvent;
import project.common.domain.DayType;
import project.common.domain.Season;
import project.common.adapter.in.web.response.PageResponse;
import project.accommodation.adapter.out.persistence.model.MainAccListQueryDto;
import project.reservation.adapter.out.persistence.model.ReservedDateQueryDto;
import project.accommodation.adapter.out.persistence.AccommodationQueryRepository;
import project.reservation.adapter.out.persistence.ReservationQueryRepository;
import project.wishlist.adapter.out.persistence.WishlistQueryRepository;
import project.infrastructure.cache.CacheService;
import project.infrastructure.time.DateManager;
import project.history.application.service.ViewHistoryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static project.accommodation.adapter.in.web.response.DetailAccommodationResDto.WishlistInfo;

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
    private final ViewHistoryService viewHistoryService;
    private final ApplicationEventPublisher eventPublisher;
    private final WishlistQueryRepository wishlistQueryRepository;
    private final ReservationQueryRepository reservationQueryRepository;
    private final AccommodationQueryRepository accommodationQueryRepository;

    @Override
    public List<MainAccResDto> getAccommodations(Long memberId) {
        LocalDate now = LocalDate.now();
        Season season = dateManager.getSeason(now);
        DayType dayType = dateManager.getDayType(now);

        List<MainAccListQueryDto> accommodations = accommodationQueryRepository.getAreaAccommodations(season, dayType, memberId);

        return accommodations.stream()
                             .collect(groupingBy(MainAccListQueryDto::getAreaKey, mapping(MainAccListResponse::from, toList())))
                             .entrySet()
                             .stream()
                             .map(entry -> new MainAccResDto(
                                     entry.getKey()
                                          .areaName(), entry.getKey()
                                                            .areaCode(), entry.getValue()
                             ))
                             .toList();
    }

    @Override
    public PageResponse<FilteredAccListResDto> getFilteredPagingAccommodations(AccommodationSearchCondition searchDto, Long memberId, Pageable pageable) {
        LocalDate now = LocalDate.now();
        Season season = dateManager.getSeason(now);
        DayType dayType = dateManager.getDayType(now);

        Page<FilteredAccListResDto> result = accommodationQueryRepository.getFilteredPagingAccommodations(searchDto, memberId, pageable, season, dayType);

        return PageResponse.from(result);
    }

    @Override
    public DetailAccommodationResDto getDetailAccommodation(Long accId, Long memberId) {
        AccommodationCommonInfo commonInfo = cacheService.getAccCommonInfo(accId);

        WishlistInfo wishlistInfo = WishlistInfo.empty();
        List<ReservedDateQueryDto> reservedDates = reservationQueryRepository.findReservedDatesByAccommodationId(accId);

        if (memberId != null) {
            eventPublisher.publishEvent(new ViewHistoryEvent(accId, memberId));
            wishlistInfo = wishlistQueryRepository.getWishlistInfo(accId, memberId)
                                                  .orElse(WishlistInfo.empty());
        }

        return DetailAccommodationResDto.from(commonInfo, wishlistInfo, reservedDates);
    }

    @Override
    public List<ViewHistoryResDto> getRecentViewAccommodations(Long memberId) {
        Map<Long, LocalDateTime> viewInfoMap = viewHistoryService.getRecentViewIdsWithTime(memberId);
        if (viewInfoMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> accIds = viewInfoMap.keySet()
                                       .stream()
                                       .toList();
        Map<Long, WishlistInfo> wishlistMap = wishlistQueryRepository.getWishlistInfos(accIds, memberId);

        List<ViewHistoryDto> historyDtos = accIds.stream()
                                                 .map(id -> {
                                                     AccommodationCommonInfo commonInfo = cacheService.getAccCommonInfo(id);
                                                     WishlistInfo wishInfo = wishlistMap.getOrDefault(id, WishlistInfo.empty());

                                                     return ViewHistoryDto.builder()
                                                                          .accommodationId(id)
                                                                          .viewDate(viewInfoMap.get(id))
                                                                          .title(commonInfo.getTitle())
                                                                          .avgRate(commonInfo.getAvgRate())
                                                                          .thumbnailUrl(commonInfo.getImages().getThumbnail())
                                                                          .isInWishlist(wishInfo.isInWishlist())
                                                                          .wishlistId(wishInfo.wishlistId())
                                                                          .wishlistName(wishInfo.wishlistName())
                                                                          .build();
                                                 })
                                                 .toList();

        return historyDtos.stream()
                          .collect(Collectors.groupingBy(
                                  dto -> dto.viewDate().toLocalDate(), LinkedHashMap::new, Collectors.toList()
                          ))
                          .entrySet()
                          .stream()
                          .map(e -> new ViewHistoryResDto(e.getKey(), e.getValue()))
                          .toList();
    }

    @Override
    public AccommodationPriceResDto getAccommodationPrice(Long accId, LocalDate date) {
        Season season = dateManager.getSeason(date);
        DayType dayType = dateManager.getDayType(date);
        int price = accommodationQueryRepository.getAccommodationPrice(accId, season, dayType);

        return new AccommodationPriceResDto(accId, date, price);
    }
}
