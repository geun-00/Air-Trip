package project.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.out.query.LoadAccommodationPort;
import project.accommodation.domain.Accommodation;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.payment.application.in.command.model.ConfirmPaymentCommand;
import project.payment.application.in.command.ConfirmPaymentUseCase;
import project.payment.application.service.model.PaymentResult;
import project.payment.application.out.api.ConfirmPaymentPort;
import project.payment.application.out.command.DeleteTempPaymentPort;
import project.payment.application.out.command.SavePaymentPort;
import project.payment.application.out.query.LoadTempPaymentPort;
import project.reservation.application.out.query.ExistsConfirmedReservationPort;
import project.reservation.application.out.query.LoadReservationPort;
import project.reservation.domain.Reservation;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentCommandService implements ConfirmPaymentUseCase {

    private final SavePaymentPort savePaymentPort;
    private final ConfirmPaymentPort confirmPaymentPort;
    private final LoadTempPaymentPort loadTempPaymentPort;
    private final LoadReservationPort loadReservationPort;
    private final DeleteTempPaymentPort deleteTempPaymentPort;
    private final LoadAccommodationPort loadAccommodationPort;
    private final ExistsConfirmedReservationPort existsConfirmedReservationPort;

    @Override
    public String confirmPayment(ConfirmPaymentCommand command, Long memberId) {
        verifyTempPayment(command.orderId(), command.amount());

        Reservation reservation = loadReservationPort.loadReservationWithLock(command.reservationId());
        Accommodation accommodation = loadAccommodationPort.loadAccommodationWithLock(reservation.getAccommodation().getId());

        if (!reservation.isOwner(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        if (existsConfirmedReservationPort.existsConfirmedReservation(
                accommodation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate())) {
            throw new BusinessException(ErrorCode.ALREADY_RESERVED);
        }

        PaymentResult paymentResult = confirmPaymentPort.confirmPayment(
                command.paymentKey(),
                command.orderId(),
                command.amount()
        );

        savePaymentPort.savePayment(paymentResult, reservation.getId());
        deleteTempPaymentPort.deleteTempPayment(command.orderId());
        reservation.confirm();

        return paymentResult.receiptUrl();
    }

    private void verifyTempPayment(String orderId, Integer amount) {
        if (loadTempPaymentPort.existsTempPaymentWithAmount(orderId, amount)) {
            throw new BusinessException(ErrorCode.NOT_EQUALS_AMOUNT);
        }
    }
}
