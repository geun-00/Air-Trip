package project.payment.application.out.command;

import project.payment.application.service.model.PaymentResult;

public interface SavePaymentPort {

    void savePayment(PaymentResult paymentResult, Long reservationId);
}
