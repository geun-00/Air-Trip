package project.review.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.common.adapter.in.web.response.PageResponse;
import project.common.application.query.PageQuery;
import project.review.adapter.in.web.response.MyReviewResponse;
import project.review.application.in.query.GetMyReviewsQueryUseCase;
import project.review.application.in.query.model.MyReviewView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewQueryController {

    private final GetMyReviewsQueryUseCase getMyReviewsQueryUseCase;

    @GetMapping("/me")
    public ResponseEntity<PageResponse<MyReviewResponse>> getMyReviews(
            @CurrentMemberId Long memberId,
            Pageable pageable
    ) {
        PageResponse<MyReviewView> result = getMyReviewsQueryUseCase.getMyReviews(
                memberId,
                new PageQuery(pageable.getPageNumber(), pageable.getPageSize())
        );

        return ResponseEntity.ok(result.map(this::convertToResponse));
    }

    private MyReviewResponse convertToResponse(MyReviewView view) {
        return new MyReviewResponse(
                view.reviewId(),
                view.accommodationId(),
                view.thumbnailUrl(),
                view.title(),
                view.content(),
                view.rating(),
                view.createdDate()
        );
    }
}
