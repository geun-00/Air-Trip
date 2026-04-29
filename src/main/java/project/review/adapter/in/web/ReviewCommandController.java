package project.review.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.review.adapter.in.web.request.PostReviewRequest;
import project.review.adapter.in.web.request.UpdateReviewRequest;
import project.review.application.in.command.CreateReviewUseCase;
import project.review.application.in.command.DeleteReviewUseCase;
import project.review.application.in.command.UpdateReviewUseCase;
import project.review.application.in.command.model.CreateReviewCommand;
import project.review.application.in.command.model.DeleteReviewCommand;
import project.review.application.in.command.model.UpdateReviewCommand;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewCommandController {

    private final CreateReviewUseCase createReviewUseCase;
    private final UpdateReviewUseCase updateReviewUseCase;
    private final DeleteReviewUseCase deleteReviewUseCase;

    @PostMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> createReview(
            @CurrentMemberId Long memberId,
            @PathVariable Long reservationId,
            @Valid @RequestBody PostReviewRequest request
    ) {
        createReviewUseCase.createReview(new CreateReviewCommand(reservationId, memberId, request.rating(), request.content()));

        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long reviewId,
            @CurrentMemberId Long memberId,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        updateReviewUseCase.updateReview(new UpdateReviewCommand(reviewId, memberId, request.rating(), request.content()));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @CurrentMemberId Long memberId
    ) {
        deleteReviewUseCase.deleteReview(new DeleteReviewCommand(reviewId, memberId));

        return ResponseEntity.ok().build();
    }
}
