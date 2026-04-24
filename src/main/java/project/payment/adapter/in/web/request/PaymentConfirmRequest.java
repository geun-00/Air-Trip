package project.payment.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import project.payment.adapter.out.api.model.PaymentConfirmDto;

public record PaymentConfirmRequest(
        @NotBlank
        String paymentKey,

        @NotBlank
        String orderId,

        @NotNull @Positive
        Integer amount,

        @NotNull
        Long reservationId)
{
    public PaymentConfirmDto convert() {
        return new PaymentConfirmDto(this.paymentKey, this.orderId, this.amount);
    }
}
