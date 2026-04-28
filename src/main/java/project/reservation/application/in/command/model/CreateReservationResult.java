package project.reservation.application.in.command.model;

import java.time.LocalDateTime;

public record CreateReservationResult(
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
