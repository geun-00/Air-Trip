package project.payment.application.out.query;

public interface LoadTempPaymentPort {

    boolean existsTempPaymentWithAmount(String orderId, Integer amount);
}
