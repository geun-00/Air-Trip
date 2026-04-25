package project.member.application.in.command.model;

import java.time.LocalDate;

public record RegisterMemberCommand(
        String name,
        String email,
        String number,
        LocalDate birthDate,
        String password
) {
}
