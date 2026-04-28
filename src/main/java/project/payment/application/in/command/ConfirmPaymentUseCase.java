package project.payment.application.in.command;

import project.payment.application.in.command.model.ConfirmPaymentCommand;

public interface ConfirmPaymentUseCase {

    String confirmPayment(ConfirmPaymentCommand command, Long memberId);
}
