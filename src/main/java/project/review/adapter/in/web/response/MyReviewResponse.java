package project.review.adapter.in.web.response;

import java.time.LocalDate;

public record MyReviewResponse(
        Long reviewId,
        Long accommodationId,
        String thumbnailUrl,
        String title,
        String content,
        double rating,
        LocalDate createdDate
) {
}
