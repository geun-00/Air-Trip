package project.auth.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.auth.adapter.out.redis.model.BlacklistedToken;
import project.auth.adapter.out.redis.model.RefreshToken;
import project.auth.application.out.command.ManageBlacklistedTokenPort;
import project.auth.application.out.command.ManageRefreshTokenPort;

@Component
@RequiredArgsConstructor
public class AuthTokenRedisAdapter implements ManageRefreshTokenPort, ManageBlacklistedTokenPort {

    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Override
    public boolean exists(String refreshToken) {
        return refreshTokenRepository.existsById(refreshToken);
    }

    @Override
    public boolean contains(String accessToken) {
        return blacklistedTokenRepository.existsById(accessToken);
    }

    @Override
    public void save(String refreshToken, Long memberId, long ttlSeconds) {
        refreshTokenRepository.save(new RefreshToken(
                refreshToken,
                memberId,
                ttlSeconds
        ));
    }

    @Override
    public void delete(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    @Override
    public void save(String accessToken, long ttlMillis) {
        blacklistedTokenRepository.save(new BlacklistedToken(accessToken, ttlMillis));
    }
}
