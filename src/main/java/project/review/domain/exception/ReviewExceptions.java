package project.review.domain.exception;

import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;

public abstract class ReviewExceptions {

    private ReviewExceptions() {
    }

    public static BusinessException notFoundReview(Long reviewId, Long memberId) {
        return new BusinessException(
                ErrorCode.ENTITY_NOT_FOUND,
                String.format("reviewId=%d, memberId=%d 후기 조회 실패", reviewId, memberId)
        );
    }
}
