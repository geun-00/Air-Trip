package project.reservation.adapter.in.web.response;

import java.time.LocalDateTime;

public record CreateReservationResponse(
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
}
