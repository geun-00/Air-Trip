package project.review.application.in.command;

import project.review.application.in.command.model.CreateReviewCommand;

public interface CreateReviewUseCase {

    void createReview(CreateReviewCommand command);
}
