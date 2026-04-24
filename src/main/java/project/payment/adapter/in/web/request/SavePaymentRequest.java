package project.payment.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SavePaymentRequest(
        @NotBlank
        String orderId,

        @NotNull @Positive
        Integer amount) {
}
