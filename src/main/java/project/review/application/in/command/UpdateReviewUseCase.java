package project.review.application.in.command;

import project.review.application.in.command.model.UpdateReviewCommand;

public interface UpdateReviewUseCase {

    void updateReview(UpdateReviewCommand command);
}
