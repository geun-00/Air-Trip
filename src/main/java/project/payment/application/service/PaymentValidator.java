package project.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.out.query.LoadAccommodationPort;
import project.accommodation.domain.Accommodation;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.payment.application.in.command.model.ConfirmPaymentCommand;
import project.payment.application.out.query.LoadTempPaymentPort;
import project.reservation.application.out.query.LoadReservationPort;
import project.reservation.domain.Reservation;

@Service
@RequiredArgsConstructor
public class PaymentValidator {

    private final LoadTempPaymentPort loadTempPaymentPort;
    private final LoadReservationPort loadReservationPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Transactional(readOnly = true)
    public void validate(ConfirmPaymentCommand command, Long memberId) {
        verifyTempPayment(command.orderId(), command.amount());

        Reservation reservation = loadReservationPort.loadOwnerReservation(command.reservationId(), memberId);
        Accommodation accommodation = loadAccommodationPort.loadAccommodation(reservation.getAccommodationId());

        if (loadReservationPort.existsConfirmedReservation(
                accommodation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate())
        ) {
            throw new BusinessException(ErrorCode.ALREADY_RESERVED);
        }
    }

    private void verifyTempPayment(String orderId, Integer amount) {
        if (!loadTempPaymentPort.existsAndIsAmountMatching(orderId, amount)) {
            throw new BusinessException(ErrorCode.NOT_EQUALS_AMOUNT);
        }
    }
}
