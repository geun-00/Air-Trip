package project.member.adapter.out.persistence.model;

import java.time.LocalDateTime;

public record ChatMemberSearchRow(
        Long id,
        String name,
        LocalDateTime createdDateTime,
        String profileImageUrl
) {
}
