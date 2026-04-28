package project.payment.application.out.command;

public interface SaveTempPaymentPort {

    void saveTempPayment(String orderId, Integer amount);
}
