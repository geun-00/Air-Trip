package project.payment.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.payment.application.service.model.PaymentResult;
import project.payment.application.out.command.SavePaymentPort;
import project.payment.domain.Payment;

@Repository
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements SavePaymentPort {

    private final PaymentRepository paymentRepository;

    @Override
    public void savePayment(PaymentResult paymentResult, Long reservationId) {
        paymentRepository.save(Payment.of(paymentResult, reservationId));
    }
}
