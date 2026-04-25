package project.member.adapter.out.persistence.model;

import project.member.domain.MemberName;

import java.time.LocalDateTime;

public record ChatMemberSearchRow(
        Long id,
        MemberName name,
        LocalDateTime createdDateTime,
        String profileImageUrl
) {
}
