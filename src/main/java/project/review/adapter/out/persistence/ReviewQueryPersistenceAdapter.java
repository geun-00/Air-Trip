package project.review.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.common.adapter.in.web.response.PageResponse;
import project.review.adapter.out.persistence.model.MyReviewRow;
import project.review.application.in.query.model.MyReviewView;
import project.review.application.out.query.GetMyReviewsPort;

@Repository
@RequiredArgsConstructor
public class ReviewQueryPersistenceAdapter implements GetMyReviewsPort {

    private final ReviewQueryRepository reviewQueryRepository;

    @Override
    public PageResponse<MyReviewView> getMyReviews(Long memberId, Pageable pageable) {
        Page<MyReviewView> views = reviewQueryRepository.getMyReviews(memberId, pageable)
                                                        .map(this::convertToView);
        return PageResponse.from(views);
    }

    private MyReviewView convertToView(MyReviewRow row) {
        return new MyReviewView(
                row.reviewId(),
                row.accommodationId(),
                row.thumbnailUrl(),
                row.title(),
                row.content(),
                row.rating().value(),
                row.createdAt().toLocalDate()
        );
    }
}
