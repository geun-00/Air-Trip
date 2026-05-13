package project.payment.application.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.out.query.ReadAccommodationPort;
import project.accommodation.domain.Accommodation;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.payment.application.out.command.SavePaymentPort;
import project.payment.application.service.PaymentProcessor;
import project.payment.application.service.model.PaymentResult;
import project.reservation.application.out.query.LoadReservationPort;
import project.reservation.domain.Reservation;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "payment.processor", havingValue = "pessimistic", matchIfMissing = true)
public class PessimisticLockPaymentProcessor implements PaymentProcessor {

    private final SavePaymentPort savePaymentPort;
    private final LoadReservationPort loadReservationPort;
    private final ReadAccommodationPort readAccommodationPort;

    @Override
    @Transactional
    public void process(Long reservationId, PaymentResult paymentResult) {
        Reservation reservation = loadReservationPort.loadReservationWithLock(reservationId);
        Accommodation accommodation = readAccommodationPort.getByIdWithLock(reservation.getAccommodationId());

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
