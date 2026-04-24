package project.member.adapter.out.persistence.model;

import java.time.LocalDateTime;

public record DefaultProfileQueryDto(
        String name,
        String profileImageUrl,
        LocalDateTime createdDateTime,
        String aboutMe,
        boolean isEmailVerified) {
}
