package project.reservation.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.reservation.application.out.query.ExistsConfirmedReservationPort;
import project.reservation.application.out.query.LoadReservationPort;
import project.reservation.domain.Reservation;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class ReservationPersistenceAdapter implements LoadReservationPort, ExistsConfirmedReservationPort {

    private final ReservationRepository reservationRepository;
    private final ReservationQueryRepository reservationQueryRepository;

    @Override
    public Reservation loadReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                                    .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }

    @Override
    public Reservation loadReservationWithLock(Long reservationId) {
        return reservationRepository.findByIdWithPessimisticLock(reservationId)
                                    .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }

    @Override
    public boolean existsConfirmedReservation(Long accommodationId, LocalDateTime startDate, LocalDateTime endDate) {
        return reservationQueryRepository.existsConfirmedReservation(accommodationId, startDate, endDate);
    }
}
