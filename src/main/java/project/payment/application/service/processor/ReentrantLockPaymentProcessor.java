package project.payment.application.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import project.payment.application.service.PaymentProcessor;
import project.payment.application.service.model.PaymentResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "payment.processor", havingValue = "reentrant")
public class ReentrantLockPaymentProcessor implements PaymentProcessor {

    private final ReentrantLockPaymentExecutor executor;

    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public void process(Long reservationId, PaymentResult paymentResult) {
        ReentrantLock lock = lockMap.computeIfAbsent(reservationId, id -> new ReentrantLock(true));
        lock.lock();
        try {
            executor.execute(reservationId, paymentResult);
        } finally {
            lock.unlock();
        }
    }
}
