package project.member.domain.support;

import java.time.LocalDate;

public record RestMemberCreateSpec(
        String name,
        String email,
        String number,
        LocalDate birthDate,
        String password
) {
}
