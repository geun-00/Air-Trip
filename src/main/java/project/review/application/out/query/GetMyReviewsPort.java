package project.review.application.out.query;

import org.springframework.data.domain.Pageable;
import project.common.adapter.in.web.response.PageResponse;
import project.review.application.in.query.model.MyReviewView;

public interface GetMyReviewsPort {

    PageResponse<MyReviewView> getMyReviews(Long memberId, Pageable pageable);
}
