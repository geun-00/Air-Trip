package project.reservation.application.in.command.model;

import java.time.LocalDate;

public record CreateReservationCommand(
        Long memberId,
        Long accommodationId,
        LocalDate startDate,
        LocalDate endDate,
        int adults,
        int children,
        int infants
) {
}
