package project.reservation.adapter.in.web.response;

import project.accommodation.domain.Accommodation;
import project.reservation.domain.Reservation;

import java.time.LocalDateTime;

public record PostReservationResponse(
        Long reservationId,
        String thumbnailUrl,
        String title,
        String refundRegulation,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int adults,
        int children,
        int infants
) {
    public static PostReservationResponse of(Accommodation accommodation, String thumbnailUrl, Reservation reservation) {
        return new PostReservationResponse(reservation.getId(), thumbnailUrl, accommodation.getTitle(), accommodation.getRefundRegulation(),
                                           reservation.getStartDate(), reservation.getEndDate(), reservation.getAdults(), reservation.getChildren(), reservation.getInfants());
    }
}
