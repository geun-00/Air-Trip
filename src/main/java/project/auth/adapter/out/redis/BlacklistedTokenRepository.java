package project.auth.adapter.out.redis;

import org.springframework.data.repository.CrudRepository;
import project.auth.adapter.out.redis.model.BlacklistedToken;

public interface BlacklistedTokenRepository extends CrudRepository<BlacklistedToken, String> {
}
