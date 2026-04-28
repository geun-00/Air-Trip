package project.review.adapter.out.persistence.model;

import java.time.LocalDateTime;

public record DetailReviewRow(
        Long accommodationId,
        Long memberId,
        String memberName,
        String profileUrl,
        LocalDateTime memberCreatedDate,
        LocalDateTime reviewCreatedDate,
        double rating,
        String content
) {
}
