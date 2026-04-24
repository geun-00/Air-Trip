package project.auth.adapter.out.redis;

import org.springframework.data.repository.CrudRepository;
import project.auth.adapter.out.redis.model.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
