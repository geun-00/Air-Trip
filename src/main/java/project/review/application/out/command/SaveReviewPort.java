package project.review.application.out.command;

import project.review.application.out.command.model.SaveReviewCommand;

public interface SaveReviewPort {

    void saveReview(SaveReviewCommand command);
}
