package project.auth.adapter.out.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.auth.application.out.command.AuthTokenPort;
import project.auth.application.out.command.model.AuthTokenClaims;
import project.auth.application.out.command.model.IssuedAuthTokens;
import project.infrastructure.jwt.JwtClaims;
import project.infrastructure.jwt.JwtProperties;
import project.infrastructure.jwt.JwtProvider;
import project.member.domain.Member;

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
    public AuthTokenClaims loadClaims(String token) {
        JwtClaims claims = jwtProvider.getClaims(token);
        return new AuthTokenClaims(claims.memberId(), claims.principalName());
    }

    @Override
    public long loadRemainingMillis(String token) {
        return jwtProvider.getRemainingMillis(token);
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
