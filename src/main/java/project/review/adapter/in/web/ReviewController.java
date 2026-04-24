package project.review.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.common.adapter.in.web.response.PageResponse;
import project.review.adapter.in.web.response.MyReviewResDto;
import project.review.adapter.in.web.request.UpdateReviewReqDto;
import project.review.application.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/me")
    public ResponseEntity<PageResponse<MyReviewResDto>> getMyReviews(@CurrentMemberId Long memberId,
                                                                     @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<MyReviewResDto> response = reviewService.getMyReviews(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable("reviewId") Long reviewId,
                                          @RequestBody UpdateReviewReqDto reqDto,
                                          @CurrentMemberId Long memberId) {
        reviewService.updateReview(reviewId, reqDto, memberId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable("reviewId") Long reviewId,
                                          @CurrentMemberId Long memberId) {
        reviewService.deleteReview(reviewId, memberId);
        return ResponseEntity.ok().build();
    }
}
