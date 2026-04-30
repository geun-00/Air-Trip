package project.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;
import project.auth.adapter.in.security.principal.JwtPrincipal;
import project.member.domain.Member;
import project.member.domain.support.RestMemberCreateSpec;

import java.util.List;
import java.util.UUID;

public class WithTestAuthSecurityContextFactory implements WithSecurityContextFactory<WithMockMember> {

    @Override
    public SecurityContext createSecurityContext(WithMockMember annotation) {
        Member member = Member.createForRest(new RestMemberCreateSpec(
                "test-user",
                "test@email.com",
                null,
                null,
                UUID.randomUUID().toString()
        ));
        ReflectionTestUtils.setField(member, "id", 1L);

        JwtPrincipal principal = new JwtPrincipal(member.getId(), "mock-principal", member.getRole());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                principal, "", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        ));

        return context;
    }
}
