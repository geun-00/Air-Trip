package project.payment.application.service.model;

import project.payment.domain.PaymentMethod;
import project.payment.domain.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResult(
        String paymentKey,
        String orderId,
        int totalAmount,
        PaymentStatus paymentStatus,
        PaymentMethod paymentMethod,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt,
        String receiptUrl
) {
}
