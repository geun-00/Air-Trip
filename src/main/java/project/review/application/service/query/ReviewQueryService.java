package project.review.application.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.common.adapter.in.web.response.PageResponse;
import project.review.application.in.query.GetMyReviewsQueryUseCase;
import project.review.application.in.query.model.MyReviewView;
import project.review.application.out.query.GetMyReviewsPort;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService implements GetMyReviewsQueryUseCase {

    private final GetMyReviewsPort getMyReviewsPort;

    @Override
    public PageResponse<MyReviewView> getMyReviews(Long memberId, Pageable pageable) {
        return getMyReviewsPort.getMyReviews(memberId, pageable);
    }
}
