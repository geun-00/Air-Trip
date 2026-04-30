package project.auth.adapter.in.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import project.auth.adapter.out.oauth.model.AuthProviderUser;
import project.auth.adapter.out.oauth.model.PrincipalUser;
import project.infrastructure.jwt.JwtProvider;
import project.member.application.out.command.LoadMemberPort;
import project.member.domain.Member;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationResolver {

    private final JwtProvider jwtProvider;
    private final LoadMemberPort loadMemberPort;

    public Authentication resolve(String token) {
        Long memberId = jwtProvider.getId(token);
        String principalName = jwtProvider.getPrincipalName(token);
        Member member = loadMemberPort.loadById(memberId);
        PrincipalUser principal = new PrincipalUser(new AuthProviderUser(member, principalName));

        return JwtAuthenticationToken.authenticated(principal, token, principal.getAuthorities());
    }
}
