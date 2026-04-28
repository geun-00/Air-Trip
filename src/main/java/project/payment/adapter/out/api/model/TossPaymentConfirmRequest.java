package project.payment.adapter.out.api.model;

public record TossPaymentConfirmRequest(
        String paymentKey,
        String orderId,
        Integer amount
) {
}
