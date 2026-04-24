package project.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;
import project.member.domain.Member;
import project.fixtures.MemberFixture;
import project.auth.adapter.out.oauth.model.AuthProviderUser;
import project.auth.adapter.out.oauth.model.PrincipalUser;

import java.util.List;

public class WithTestAuthSecurityContextFactory implements WithSecurityContextFactory<WithMockMember> {

    @Override
    public SecurityContext createSecurityContext(WithMockMember annotation) {
        Member member = MemberFixture.create();
        ReflectionTestUtils.setField(member, "id", 1L);

        PrincipalUser principalUser = new PrincipalUser(new AuthProviderUser(member, "mock-principal"));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                principalUser, "", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        ));

        return context;
    }
}
