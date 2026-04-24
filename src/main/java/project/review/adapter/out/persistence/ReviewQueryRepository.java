package project.review.adapter.out.persistence;

import com.querydsl.core.types.Projections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.review.adapter.in.web.response.MyReviewResDto;
import project.review.domain.Review;

import static project.accommodation.domain.QAccommodation.accommodation;
import static project.accommodation.domain.QAccommodationImage.accommodationImage;
import static project.reservation.domain.QReservation.reservation;
import static project.review.domain.QReview.review;

@Repository
public class ReviewQueryRepository extends CustomQuerydslRepositorySupport {

    public ReviewQueryRepository() {
        super(Review.class);
    }

    public Page<MyReviewResDto> getMyReviews(Long memberId, Pageable pageable) {
        return applyPagination(pageable,
                contentQuery -> contentQuery
                        .select(Projections.constructor(MyReviewResDto.class,
                                review.id,
                                accommodation.id,
                                accommodationImage.imageUrl,
                                accommodation.title,
                                review.content,
                                review.rating,
                                review.createdAt
                        ))
                        .from(review)
                        .join(review.reservation, reservation)
                        .join(reservation.accommodation, accommodation)
                        .join(accommodationImage)
                        .on(accommodationImage.accommodation.eq(accommodation)
                                                            .and(accommodationImage.thumbnail.isTrue()))
                        .where(review.member.id.eq(memberId))
                ,
                countQuery -> countQuery.select(review.count())
                                        .from(review)
                                        .where(review.member.id.eq(memberId))
        );
    }
}
