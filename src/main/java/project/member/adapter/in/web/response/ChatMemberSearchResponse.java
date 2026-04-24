package project.member.adapter.in.web.response;

import java.time.LocalDateTime;

public record ChatMemberSearchResponse(
        Long id,
        String name,
        LocalDateTime createdDateTime,
        String profileImageUrl) {
}
