package project.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.adapter.out.persistence.BaseEntity;
import project.payment.application.service.model.PaymentResult;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Id
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private LocalDateTime approvedAt;

    @Column(nullable = false)
    private Long reservationId;

    public static Payment of(PaymentResult paymentResult, Long reservationId) {
        return new Payment(
                paymentResult.paymentKey(),
                paymentResult.orderId(),
                paymentResult.totalAmount(),
                paymentResult.paymentStatus(),
                paymentResult.requestedAt(),
                paymentResult.paymentMethod(),
                paymentResult.approvedAt(),
                reservationId
        );
    }
}
