package project.review.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.common.adapter.in.web.response.PageResponse;
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
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<MyReviewView> result = getMyReviewsQueryUseCase.getMyReviews(memberId, pageable);

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
