package project.review.application.out.command.model;

import java.math.BigDecimal;

public record SaveReviewCommand(
        Long reservationId,
        Long memberId,
        BigDecimal rating,
        String content
) {
}
