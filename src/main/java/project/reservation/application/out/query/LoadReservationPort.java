package project.reservation.application.out.query;

import project.reservation.domain.Reservation;

import java.time.LocalDateTime;

public interface LoadReservationPort {

    Reservation loadReservation(Long reservationId);

    Reservation loadReservationWithLock(Long reservationId);

    boolean existsConfirmedReservation(Long accommodationId, LocalDateTime startDate, LocalDateTime endDate);
}
