package project.reservation.application.out.query;

import project.reservation.domain.Reservation;

public interface LoadReservationPort {

    Reservation loadReservation(Long reservationId);

    Reservation loadReservationWithLock(Long reservationId);
}
