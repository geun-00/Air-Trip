package project.auth.adapter.in.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import project.auth.adapter.in.security.principal.JwtPrincipal;
import project.infrastructure.jwt.JwtClaims;
import project.infrastructure.jwt.JwtProvider;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationResolver {

    private final JwtProvider jwtProvider;

    public Authentication resolve(String token) {
        JwtClaims claims = jwtProvider.getClaims(token);
        JwtPrincipal principal = new JwtPrincipal(claims.memberId(), claims.principalName(), claims.role());

        return JwtAuthenticationToken.authenticated(principal, token, principal.getAuthorities());
    }
}
