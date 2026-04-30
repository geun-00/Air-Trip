package project.auth.adapter.in.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import project.auth.adapter.in.security.principal.JwtPrincipal;
import project.infrastructure.jwt.JwtProvider;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationResolver {

    private final JwtProvider jwtProvider;

    public Authentication resolve(String token) {
        Long memberId = jwtProvider.getId(token);
        String principalName = jwtProvider.getPrincipalName(token);
        JwtPrincipal principal = new JwtPrincipal(memberId, principalName, jwtProvider.getRole(token));

        return JwtAuthenticationToken.authenticated(principal, token, principal.getAuthorities());
    }
}
