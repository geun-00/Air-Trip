package project.member.application.in.command.model;

import java.time.LocalDate;

public record RegisterSocialMemberCommand(
        String email,
        String provider,
        String password,
        String name,
        String number,
        LocalDate birthDate
) {
}
