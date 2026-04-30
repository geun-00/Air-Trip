package project.infrastructure.jwt;

import project.member.domain.Role;

public record JwtClaims(
        Long memberId,
        String principalName,
        Role role
) {
}
