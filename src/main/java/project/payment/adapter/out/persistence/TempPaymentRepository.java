package project.payment.adapter.out.persistence;

import project.common.adapter.out.persistence.repository.RedisPersistenceRepository;
import org.springframework.data.repository.CrudRepository;
import project.payment.adapter.out.persistence.model.TempPayment;

@RedisPersistenceRepository
public interface TempPaymentRepository extends CrudRepository<TempPayment, String> {
}
