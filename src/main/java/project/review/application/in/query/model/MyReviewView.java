package project.review.application.in.query.model;

import java.time.LocalDate;

public record MyReviewView(
        Long reviewId,
        Long accommodationId,
        String thumbnailUrl,
        String title,
        String content,
        double rating,
        LocalDate createdDate
) {
}
