package project.review.application.in.command.model;

import java.math.BigDecimal;

public record UpdateReviewCommand(
        Long reviewId,
        Long memberId,
        BigDecimal rating,
        String content
) {
}
