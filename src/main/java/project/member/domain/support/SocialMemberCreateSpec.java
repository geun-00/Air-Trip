package project.member.domain.support;

import project.member.domain.SocialType;

import java.time.LocalDate;

public record SocialMemberCreateSpec(
        String name,
        String email,
        String number,
        LocalDate birthDate,
        String password,
        SocialType socialType
) {
}
