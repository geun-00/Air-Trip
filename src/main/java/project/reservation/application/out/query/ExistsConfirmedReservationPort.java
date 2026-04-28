package project.reservation.application.out.query;

import java.time.LocalDateTime;

public interface ExistsConfirmedReservationPort {

    boolean existsConfirmedReservation(Long accommodationId, LocalDateTime startDate, LocalDateTime endDate);
}
