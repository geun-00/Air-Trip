package project.member.adapter.out.redis;

import project.common.adapter.out.persistence.repository.RedisPersistenceRepository;
import org.springframework.data.repository.CrudRepository;
import project.member.adapter.out.redis.model.EmailVerification;

@RedisPersistenceRepository
public interface EmailVerificationRepository extends CrudRepository<EmailVerification, String> {
}
