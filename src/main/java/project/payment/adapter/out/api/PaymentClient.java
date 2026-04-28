package project.payment.adapter.out.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import project.payment.adapter.out.api.model.TossPaymentConfirmRequest;

@HttpExchange
public interface PaymentClient {

    @PostExchange("/confirm")
    JsonNode confirmPayment(@RequestBody TossPaymentConfirmRequest request);
}
