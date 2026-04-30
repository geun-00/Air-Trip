package project.auth.adapter.in.security.principal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import project.member.domain.Role;

import java.util.List;

public record JwtPrincipal(
        Long memberId,
        String principalName,
        Role role
) implements AuthenticatedMember {

    public List<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getRoleName()));
    }
}
