package project.member.application.query.model;

import java.time.LocalDate;

public record DefaultProfileView(
        String name,
        String profileImageUrl,
        LocalDate createdDate,
        String aboutMe,
        boolean isEmailVerified
) {
}
