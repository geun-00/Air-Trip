package project.payment.adapter.out.persistence;

import org.springframework.data.repository.CrudRepository;
import project.payment.adapter.out.persistence.model.TempPayment;

public interface TempPaymentRepository extends CrudRepository<TempPayment, String> {
}
