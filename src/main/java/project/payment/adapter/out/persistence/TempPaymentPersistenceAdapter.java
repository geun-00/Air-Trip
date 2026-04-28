package project.payment.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.payment.adapter.out.persistence.model.TempPayment;
import project.payment.application.out.command.DeleteTempPaymentPort;
import project.payment.application.out.command.SaveTempPaymentPort;
import project.payment.application.out.query.LoadTempPaymentPort;

@Repository
@RequiredArgsConstructor
public class TempPaymentPersistenceAdapter implements SaveTempPaymentPort, LoadTempPaymentPort, DeleteTempPaymentPort {

    private final TempPaymentRepository tempPaymentRepository;

    @Override
    public void saveTempPayment(String orderId, Integer amount) {
        tempPaymentRepository.save(TempPayment.of(orderId, amount));
    }

    @Override
    public boolean existsAndIsAmountMatching(String orderId, Integer amount) {
        return tempPaymentRepository.findById(orderId)
                                    .map(tempPayment -> tempPayment.isEqualsAmount(amount))
                                    .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Override
    public void deleteTempPayment(String orderId) {
        tempPaymentRepository.deleteById(orderId);
    }
}
