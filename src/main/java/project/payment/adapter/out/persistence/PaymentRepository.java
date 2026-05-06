package project.payment.adapter.out.persistence;

import project.common.adapter.out.persistence.repository.JpaPersistenceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import project.payment.domain.Payment;

@JpaPersistenceRepository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
