package project.payment.application.in.command;

import project.payment.application.in.command.model.SaveTempPaymentCommand;

public interface SaveTempPaymentUseCase {

    void saveTempPayment(SaveTempPaymentCommand command);
}
