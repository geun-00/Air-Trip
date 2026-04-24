package project.payment.adapter.out.api.model;

public record PaymentConfirmDto(
        String paymentKey,
        String orderId,
        Integer amount) {
}
