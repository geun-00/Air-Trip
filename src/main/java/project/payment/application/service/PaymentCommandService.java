package project.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.payment.application.in.command.ConfirmPaymentUseCase;
import project.payment.application.in.command.model.ConfirmPaymentCommand;
import project.payment.application.out.api.ConfirmPaymentPort;
import project.payment.application.out.command.DeleteTempPaymentPort;
import project.payment.application.service.model.PaymentResult;

@Service
@RequiredArgsConstructor
public class PaymentCommandService implements ConfirmPaymentUseCase {

    private final PaymentValidator paymentValidator;
    private final PaymentProcessor paymentProcessor;
    private final ConfirmPaymentPort confirmPaymentPort;
    private final DeleteTempPaymentPort deleteTempPaymentPort;

    @Override
    public String confirmPayment(ConfirmPaymentCommand command, Long memberId) {
        paymentValidator.validate(command, memberId);

        PaymentResult paymentResult = confirmPaymentPort.confirmPayment(
                command.paymentKey(),
                command.orderId(),
                command.amount()
        );

        paymentProcessor.process(command.reservationId(), paymentResult);
        deleteTempPaymentPort.deleteTempPayment(command.orderId());

        return paymentResult.receiptUrl();
    }
}
