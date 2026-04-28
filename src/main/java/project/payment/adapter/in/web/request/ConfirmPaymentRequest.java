package project.payment.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ConfirmPaymentRequest(
        @NotBlank
        String paymentKey,

        @NotBlank
        String orderId,

        @NotNull @Positive
        Integer amount,

        @NotNull
        Long reservationId
) {
}
