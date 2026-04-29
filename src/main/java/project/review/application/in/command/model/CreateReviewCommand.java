package project.review.application.in.command.model;

import java.math.BigDecimal;

public record CreateReviewCommand(
        Long reservationId,
        Long memberId,
        BigDecimal rating,
        String content
) {
}
