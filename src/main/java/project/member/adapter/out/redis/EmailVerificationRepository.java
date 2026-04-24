package project.member.adapter.out.redis;

import org.springframework.data.repository.CrudRepository;
import project.member.adapter.out.redis.model.EmailVerification;

public interface EmailVerificationRepository extends CrudRepository<EmailVerification, String> {
}
