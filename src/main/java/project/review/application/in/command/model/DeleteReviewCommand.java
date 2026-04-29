package project.review.application.in.command.model;

public record DeleteReviewCommand(
        Long reviewId,
        Long memberId
) {
}
