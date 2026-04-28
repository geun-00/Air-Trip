package project.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.out.query.LoadAccommodationPort;
import project.accommodation.domain.Accommodation;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.payment.application.out.command.SavePaymentPort;
import project.payment.application.service.model.PaymentResult;
import project.reservation.application.out.query.ExistsConfirmedReservationPort;
import project.reservation.application.out.query.LoadReservationPort;
import project.reservation.domain.Reservation;

@Service
@RequiredArgsConstructor
public class PaymentProcessor {

    private final SavePaymentPort savePaymentPort;
    private final LoadReservationPort loadReservationPort;
    private final LoadAccommodationPort loadAccommodationPort;
    private final ExistsConfirmedReservationPort existsConfirmedReservationPort;

    @Transactional
    public void process(Long reservationId, PaymentResult paymentResult) {
        Reservation reservation = loadReservationPort.loadReservationWithLock(reservationId);
        Accommodation accommodation = loadAccommodationPort.loadAccommodationWithLock(reservation.getAccommodation().getId());

        if (existsConfirmedReservationPort.existsConfirmedReservation(
                accommodation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate())
        ) {
            throw new BusinessException(ErrorCode.ALREADY_RESERVED);
        }

        reservation.confirm();
        savePaymentPort.savePayment(paymentResult, reservation.getId());
    }
}
