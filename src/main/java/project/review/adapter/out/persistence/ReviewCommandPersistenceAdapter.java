package project.review.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.review.application.out.command.DeleteReviewPort;
import project.review.application.out.command.LoadReviewPort;
import project.review.application.out.command.SaveReviewPort;
import project.review.application.out.command.model.SaveReviewCommand;
import project.review.domain.Review;
import project.review.domain.exception.ReviewExceptions;

@Repository
@RequiredArgsConstructor
public class ReviewCommandPersistenceAdapter implements SaveReviewPort,
                                                        LoadReviewPort,
                                                        DeleteReviewPort {

    private final ReviewRepository reviewRepository;

    @Override
    public void saveReview(SaveReviewCommand command) {
        reviewRepository.save(Review.create(
                command.rating().doubleValue(),
                command.content(),
                command.reservationId(),
                command.memberId()
        ));
    }

    @Override
    public Review loadOwnerReview(Long reviewId, Long memberId) {
        return reviewRepository.findByIdAndMemberId(reviewId, memberId)
                               .orElseThrow(() -> ReviewExceptions.notFoundReview(reviewId, memberId));
    }

    @Override
    public void delete(Review review) {
        reviewRepository.delete(review);
    }
}
