package project.payment.application.out.query;

public interface LoadTempPaymentPort {

    boolean existsAndIsAmountMatching(String orderId, Integer amount);
}
