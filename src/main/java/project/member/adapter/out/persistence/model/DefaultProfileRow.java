package project.member.adapter.out.persistence.model;

import project.member.domain.AboutMe;
import project.member.domain.MemberName;

import java.time.LocalDateTime;

public record DefaultProfileRow(
        MemberName name,
        String profileImageUrl,
        LocalDateTime createdDateTime,
        AboutMe aboutMe,
        boolean isEmailVerified
) {
}
