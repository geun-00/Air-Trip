package project.review.application.out.command;

import project.review.domain.Review;

public interface DeleteReviewPort {

    void delete(Review review);
}
