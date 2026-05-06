package project.auth.adapter.out.redis;

import project.common.adapter.out.persistence.repository.RedisPersistenceRepository;
import org.springframework.data.repository.CrudRepository;
import project.auth.adapter.out.redis.model.RefreshToken;

@RedisPersistenceRepository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
