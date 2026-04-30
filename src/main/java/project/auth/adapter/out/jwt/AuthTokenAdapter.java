package project.auth.adapter.out.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.auth.application.out.command.AuthTokenPort;
import project.auth.application.out.command.model.IssuedAuthTokens;
import project.infrastructure.jwt.JwtProperties;
import project.infrastructure.jwt.JwtProvider;
import project.member.domain.Member;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthTokenAdapter implements AuthTokenPort {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    @Override
    public void validate(String token) {
        jwtProvider.validateToken(token);
    }

    @Override
    public Long loadMemberId(String token) {
        return jwtProvider.getId(token);
    }

    @Override
    public String loadPrincipalName(String token) {
        return jwtProvider.getPrincipalName(token);
    }

    @Override
    public long loadRemainingMillis(String token) {
        try {
            Date expiration = jwtProvider.getExpiration(token);
            return expiration.getTime() - new Date().getTime();
        } catch (ExpiredJwtException ignored) {
            return 0;
        }
    }

    @Override
    public IssuedAuthTokens issue(Member member, String principalName) {
        return new IssuedAuthTokens(
                jwtProvider.generateAccessToken(member, principalName),
                jwtProvider.generateRefreshToken(member, principalName),
                jwtProperties.getAccessToken().getExpiration(),
                jwtProperties.getRefreshToken().getExpiration()
        );
    }
}
