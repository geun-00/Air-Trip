package project.reservation.application.out.query;

import project.reservation.domain.Reservation;

import java.time.LocalDateTime;

public interface LoadReservationPort {

    Reservation loadReservation(Long reservationId);

    Reservation loadOwnerReservation(Long reservationId, Long memberId);

    Reservation loadReservationWithLock(Long reservationId);

    Reservation loadReservationWithOptimisticLock(Long reservationId);

    boolean existsConfirmedReservation(Long accommodationId, LocalDateTime startDate, LocalDateTime endDate);
}
