package project.review.adapter.out.persistence.model;

import project.common.domain.Rating;

import java.time.LocalDateTime;

public record DetailReviewRow(
        Long accommodationId,
        Long memberId,
        String memberName,
        String profileUrl,
        LocalDateTime memberCreatedDate,
        LocalDateTime reviewCreatedDate,
        Rating rating,
        String content
) {
}
