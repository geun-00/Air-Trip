package project.payment.adapter.out.api;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import project.payment.adapter.out.api.model.TossPaymentConfirmRequest;
import project.payment.application.service.model.PaymentResult;
import project.payment.application.out.api.ConfirmPaymentPort;
import project.payment.domain.PaymentMethod;
import project.payment.domain.PaymentStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class PaymentApiAdapter implements ConfirmPaymentPort {

    private static final ZoneId ZONE_ASIA_SEOUL = ZoneId.of("Asia/Seoul");

    private final PaymentClient paymentClient;

    @Override
    public PaymentResult confirmPayment(String paymentKey, String orderId, Integer amount) {
        JsonNode response = paymentClient.confirmPayment(new TossPaymentConfirmRequest(paymentKey, orderId, amount));
        return toPaymentResult(response);
    }

    private PaymentResult toPaymentResult(JsonNode response) {
        return new PaymentResult(
                response.get("paymentKey").asText(),
                response.get("orderId").asText(),
                response.get("totalAmount").asInt(),
                PaymentStatus.of(response.get("status").asText()),
                PaymentMethod.of(response.get("method").asText()),
                parseToLocalDateTime(response.get("requestedAt").asText()),
                parseToLocalDateTime(response.get("approvedAt").asText(null)),
                response.get("receipt").get("url").asText(null)
        );
    }

    private LocalDateTime parseToLocalDateTime(String timestamp) {
        if (!StringUtils.hasText(timestamp)) {
            return null;
        }
        return OffsetDateTime.parse(timestamp).atZoneSameInstant(ZONE_ASIA_SEOUL).toLocalDateTime();
    }
}
