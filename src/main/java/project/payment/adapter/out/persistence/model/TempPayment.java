package project.payment.adapter.out.persistence.model;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@RedisHash(value = "payment:temp", timeToLive = 600)
public class TempPayment {

    @Id
    private String orderId;

    private Integer amount;

    public static TempPayment of(String orderId, Integer amount) {
        return new TempPayment(orderId, amount);
    }

    public boolean isEqualsAmount(Integer amount) {
        return this.amount.equals(amount);
    }
}
