package project.review.application.out.query;

import project.common.adapter.in.web.response.PageResponse;
import project.common.application.query.PageQuery;
import project.review.application.in.query.model.MyReviewView;

public interface GetMyReviewsPort {

    PageResponse<MyReviewView> getMyReviews(Long memberId, PageQuery pageQuery);
}
