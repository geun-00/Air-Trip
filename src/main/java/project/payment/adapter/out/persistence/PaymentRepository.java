package project.payment.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import project.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
