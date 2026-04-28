package project.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.payment.application.in.command.model.SaveTempPaymentCommand;
import project.payment.application.in.command.SaveTempPaymentUseCase;
import project.payment.application.out.command.SaveTempPaymentPort;

@Service
@RequiredArgsConstructor
public class TempPaymentCommandService implements SaveTempPaymentUseCase {

    private final SaveTempPaymentPort saveTempPaymentPort;

    @Override
    public void saveTempPayment(SaveTempPaymentCommand command) {
        saveTempPaymentPort.saveTempPayment(command.orderId(), command.amount());
    }
}
