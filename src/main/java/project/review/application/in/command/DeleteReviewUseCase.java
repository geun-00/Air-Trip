package project.review.application.in.command;

import project.review.application.in.command.model.DeleteReviewCommand;

public interface DeleteReviewUseCase {

    void deleteReview(DeleteReviewCommand command);
}
