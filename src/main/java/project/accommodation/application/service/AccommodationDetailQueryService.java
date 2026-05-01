package project.accommodation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.in.query.ReadAccommodationDetailUseCase;
import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.accommodation.application.in.query.model.AccommodationDetailView;
import project.accommodation.application.out.query.ReadAccommodationCommonInfoPort;
import project.accommodation.application.out.query.ReadAccommodationWishlistPort;
import project.accommodation.application.out.query.ReadReservedDatesPort;
import project.accommodation.application.service.model.AccommodationWishlistState;
import project.history.application.event.ViewHistoryEvent;

import java.util.List;

import static project.accommodation.application.in.query.model.AccommodationDetailView.ReservedDateView;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationDetailQueryService implements ReadAccommodationDetailUseCase {

    private final ApplicationEventPublisher eventPublisher;
    private final ReadReservedDatesPort readReservedDatesPort;
    private final ReadAccommodationWishlistPort readAccommodationWishlistPort;
    private final ReadAccommodationCommonInfoPort readAccommodationCommonInfoPort;

    @Override
    public AccommodationDetailView getDetailAccommodation(Long accommodationId, Long memberId) {
        AccommodationCommonInfoView commonInfo = readAccommodationCommonInfoPort.getById(accommodationId);
        AccommodationWishlistState wishlistState = getWishlistState(accommodationId, memberId);
        List<ReservedDateView> reservedDates = getReservedDates(accommodationId);

        publishViewHistory(accommodationId, memberId);

        // TODO : 조립 매퍼 클래스 사용
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
                wishlistState.isInWishlist(),
                wishlistState.wishlistId(),
                wishlistState.wishlistName(),
                commonInfo.getAvgRate(),
                commonInfo.getImages(),
                commonInfo.getAmenities(),
                commonInfo.getReviews(),
                reservedDates
        );
    }

    private AccommodationWishlistState getWishlistState(Long accommodationId, Long memberId) {
        if (memberId == null) {
            return AccommodationWishlistState.empty();
        }

        return readAccommodationWishlistPort.findByAccommodationIdAndMemberId(accommodationId, memberId)
                                            .map(AccommodationWishlistState::from)
                                            .orElseGet(AccommodationWishlistState::empty);
    }

    private List<ReservedDateView> getReservedDates(Long accommodationId) {
        return readReservedDatesPort.getByAccommodationId(accommodationId)
                                    .stream()
                                    .map(date -> new ReservedDateView(date.startDate(), date.endDate()))
                                    .toList();
    }

    private void publishViewHistory(Long accommodationId, Long memberId) {
        if (memberId == null) {
            return;
        }

        eventPublisher.publishEvent(new ViewHistoryEvent(accommodationId, memberId));
    }
}
