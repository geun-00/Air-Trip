package project.review.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.review.domain.exception.ReviewExceptions;
import project.common.adapter.in.web.response.PageResponse;
import project.review.adapter.in.web.response.MyReviewResDto;
import project.review.adapter.in.web.request.UpdateReviewReqDto;
import project.review.domain.Review;
import project.review.adapter.out.persistence.ReviewRepository;
import project.review.adapter.out.persistence.ReviewQueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;

    public PageResponse<MyReviewResDto> getMyReviews(Long memberId, Pageable pageable) {
        Page<MyReviewResDto> result = reviewQueryRepository.getMyReviews(memberId, pageable);

        return PageResponse.from(result);
    }

    @Transactional
    public void updateReview(Long reviewId, UpdateReviewReqDto reqDto, Long memberId) {
        Review review = getReview(reviewId, memberId);
        review.update(reqDto.rating().doubleValue(), reqDto.content());
    }

    @Transactional
    public void deleteReview(Long reviewId, Long memberId) {
        Review review = getReview(reviewId, memberId);
        reviewRepository.delete(review);
    }

    private Review getReview(Long reviewId, Long memberId) {
        return reviewRepository.findByIdAndMemberId(reviewId, memberId)
                               .orElseThrow(() -> ReviewExceptions.notFoundReview(reviewId, memberId));
    }
}
