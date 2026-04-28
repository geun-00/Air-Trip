package project.reservation.application.out.query;

import project.reservation.domain.Reservation;

public interface LoadReservationPort {

    Reservation loadReservationWithLock(Long reservationId);
}
