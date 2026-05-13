package project.payment.application.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.out.query.ReadAccommodationPort;
import project.accommodation.domain.Accommodation;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.payment.application.out.command.SavePaymentPort;
import project.payment.application.service.model.PaymentResult;
import project.reservation.application.out.query.LoadReservationPort;
import project.reservation.domain.Reservation;

@Component
@RequiredArgsConstructor
public class ReentrantLockPaymentExecutor {

    private final SavePaymentPort savePaymentPort;
    private final LoadReservationPort loadReservationPort;
    private final ReadAccommodationPort readAccommodationPort;

    @Transactional
    public void execute(Long reservationId, PaymentResult paymentResult) {
        Reservation reservation = loadReservationPort.loadReservation(reservationId);
        Accommodation accommodation = readAccommodationPort.getById(reservation.getAccommodationId());

        if (loadReservationPort.existsConfirmedReservation(
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
