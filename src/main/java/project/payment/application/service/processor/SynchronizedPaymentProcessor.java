package project.payment.application.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import project.payment.application.service.PaymentProcessor;
import project.payment.application.service.model.PaymentResult;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "payment.processor", havingValue = "synchronized")
public class SynchronizedPaymentProcessor implements PaymentProcessor {

    private final SynchronizedPaymentExecutor executor;

    @Override
    public synchronized void process(Long reservationId, PaymentResult paymentResult) {
        executor.execute(reservationId, paymentResult);
    }
}
