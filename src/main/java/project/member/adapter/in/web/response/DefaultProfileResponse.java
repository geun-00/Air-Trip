package project.member.adapter.in.web.response;

import java.time.LocalDate;

public record DefaultProfileResponse(
        String name,
        String profileImageUrl,
        LocalDate createdDate,
        String aboutMe,
        boolean isEmailVerified) {
}
