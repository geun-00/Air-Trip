package project.auth.adapter.out.redis;

import project.common.adapter.out.persistence.repository.RedisPersistenceRepository;
import org.springframework.data.repository.CrudRepository;
import project.auth.adapter.out.redis.model.BlacklistedToken;

@RedisPersistenceRepository
public interface BlacklistedTokenRepository extends CrudRepository<BlacklistedToken, String> {
}
