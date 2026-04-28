package project.payment.application.in.command.model;

public record ConfirmPaymentCommand(
        String paymentKey,
        String orderId,
        Integer amount,
        Long reservationId
) {
}
