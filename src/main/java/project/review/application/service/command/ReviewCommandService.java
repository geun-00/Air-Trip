package project.review.application.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.out.command.EvictAccommodationCommonInfoPort;
import project.reservation.application.out.query.LoadReservationPort;
import project.reservation.domain.Reservation;
import project.review.application.in.command.CreateReviewUseCase;
import project.review.application.in.command.DeleteReviewUseCase;
import project.review.application.in.command.UpdateReviewUseCase;
import project.review.application.in.command.model.CreateReviewCommand;
import project.review.application.in.command.model.DeleteReviewCommand;
import project.review.application.in.command.model.UpdateReviewCommand;
import project.review.application.out.command.DeleteReviewPort;
import project.review.application.out.command.LoadReviewPort;
import project.review.application.out.command.SaveReviewPort;
import project.review.application.out.command.model.SaveReviewCommand;
import project.review.domain.Review;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandService implements CreateReviewUseCase,
                                             UpdateReviewUseCase,
                                             DeleteReviewUseCase {

    private final SaveReviewPort saveReviewPort;
    private final LoadReviewPort loadReviewPort;
    private final DeleteReviewPort deleteReviewPort;
    private final LoadReservationPort loadReservationPort;
    private final EvictAccommodationCommonInfoPort evictAccommodationCommonInfoPort;

    @Override
    public void createReview(CreateReviewCommand command) {
        Reservation reservation = loadReservationPort.loadOwnerReservation(
                command.reservationId(),
                command.memberId()
        );

        saveReviewPort.saveReview(new SaveReviewCommand(
                command.reservationId(),
                command.memberId(),
                command.rating(),
                command.content()
        ));

        evictAccommodationCommonInfoPort.evictAccommodationCommonInfo(reservation.getAccommodationId());
    }

    @Override
    public void updateReview(UpdateReviewCommand command) {
        Review review = loadReviewPort.loadOwnerReview(command.reviewId(), command.memberId());
        review.update(command.rating().doubleValue(), command.content());

        evictAccommodationCommonInfo(review.getReservationId());
    }

    @Override
    public void deleteReview(DeleteReviewCommand command) {
        Review review = loadReviewPort.loadOwnerReview(command.reviewId(), command.memberId());
        deleteReviewPort.delete(review);

        evictAccommodationCommonInfo(review.getReservationId());
    }

    private void evictAccommodationCommonInfo(Long reservationId) {
        Reservation reservation = loadReservationPort.loadReservation(reservationId);
        evictAccommodationCommonInfoPort.evictAccommodationCommonInfo(reservation.getAccommodationId());
    }
}
