package project.review.adapter.out.persistence.model;

import project.common.domain.Rating;

import java.time.LocalDateTime;

public record MyReviewRow(
        Long reviewId,
        Long accommodationId,
        String thumbnailUrl,
        String title,
        String content,
        Rating rating,
        LocalDateTime createdAt
) {
}
