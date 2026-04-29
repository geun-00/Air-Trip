package project.review.application.out.command;

import project.review.domain.Review;

public interface LoadReviewPort {

    Review loadOwnerReview(Long reviewId, Long memberId);
}
