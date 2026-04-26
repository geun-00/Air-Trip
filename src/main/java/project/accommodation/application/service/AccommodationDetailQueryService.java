package project.accommodation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.in.query.GetAccommodationDetailQueryUseCase;
import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.accommodation.application.in.query.model.AccommodationDetailView;
import project.accommodation.application.out.query.LoadAccommodationWishlistPort;
import project.accommodation.application.out.query.LoadReservedDatesPort;
import project.accommodation.application.out.query.model.ReservedDateView;
import project.accommodation.application.out.query.model.WishlistInfoView;
import project.history.application.event.ViewHistoryEvent;
import project.infrastructure.cache.CacheService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationDetailQueryService implements GetAccommodationDetailQueryUseCase {

    private final CacheService cacheService;
    private final ApplicationEventPublisher eventPublisher;
    private final LoadReservedDatesPort loadReservedDatesPort;
    private final LoadAccommodationWishlistPort loadAccommodationWishlistPort;

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
}
