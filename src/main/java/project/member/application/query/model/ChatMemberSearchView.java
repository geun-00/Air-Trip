package project.member.application.query.model;

import java.time.LocalDateTime;

public record ChatMemberSearchView(
        Long id,
        String name,
        LocalDateTime createdDateTime,
        String profileImageUrl
) {
}
