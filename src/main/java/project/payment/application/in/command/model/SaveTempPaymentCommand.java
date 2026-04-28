package project.payment.application.in.command.model;

public record SaveTempPaymentCommand(
        String orderId,
        Integer amount
) {
}
