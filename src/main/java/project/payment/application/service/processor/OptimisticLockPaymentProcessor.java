package project.payment.application.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.payment.application.service.PaymentProcessor;
import project.payment.application.service.model.PaymentResult;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "payment.processor", havingValue = "optimistic")
public class OptimisticLockPaymentProcessor implements PaymentProcessor {

    private static final int MAX_RETRY = 3;

    private final OptimisticLockPaymentExecutor executor;

    @Override
    public void process(Long reservationId, PaymentResult paymentResult) {
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                executor.execute(reservationId, paymentResult);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("[OptimisticLock] 충돌 감지 — reservationId={}, attempt={}/{}", reservationId, attempt, MAX_RETRY);
                if (attempt == MAX_RETRY) {
                    throw new BusinessException(ErrorCode.ALREADY_RESERVED);
                }
            }
        }
    }
}
