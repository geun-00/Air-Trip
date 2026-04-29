package project.reservation.adapter.in.web.request;

import java.time.LocalDate;

public record CreateReservationRequest(
        LocalDate startDate,
        LocalDate endDate,
        int adults,
        int children,
        int infants
) {
}
