package project.payment.application.out.api;

import project.payment.application.service.model.PaymentResult;

public interface ConfirmPaymentPort {

    PaymentResult confirmPayment(String paymentKey, String orderId, Integer amount);
}
