package project.reservation.application.out.command.model;

import java.time.LocalDateTime;

public record SaveReservationCommand(
        Long memberId,
        Long accommodationId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int adults,
        int children,
        int infants
) {
}
