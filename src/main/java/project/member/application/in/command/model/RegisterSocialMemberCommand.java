package project.member.application.in.command.model;

import java.time.LocalDate;

public record RegisterSocialMemberCommand(
        String name,
        String email,
        String provider,
        String password,
        String number,
        LocalDate birthDate,
        String imageUrl
) {
}
