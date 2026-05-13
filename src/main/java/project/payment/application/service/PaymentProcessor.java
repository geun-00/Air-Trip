package project.payment.application.service;

import project.payment.application.service.model.PaymentResult;

public interface PaymentProcessor {

    void process(Long reservationId, PaymentResult paymentResult);
}
