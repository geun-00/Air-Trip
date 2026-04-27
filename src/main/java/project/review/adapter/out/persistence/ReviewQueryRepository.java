package project.review.adapter.out.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.review.adapter.out.persistence.model.DetailReviewRow;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.review.adapter.in.web.response.MyReviewResDto;
import project.review.domain.Review;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static project.accommodation.domain.QAccommodation.accommodation;
import static project.accommodation.domain.QAccommodationImage.accommodationImage;
import static project.member.domain.QMember.member;
import static project.reservation.domain.QReservation.reservation;
import static project.review.domain.QReview.review;

@Repository
public class ReviewQueryRepository extends CustomQuerydslRepositorySupport {

    private static final StringPath MEMBER_NAME = Expressions.stringPath(member, "name");

    public ReviewQueryRepository() {
        super(Review.class);
    }

    public List<DetailReviewRow> findReviewsByAccommodationId(Long accommodationId) {
        return select(constructor(DetailReviewRow.class,
                member.id,
                MEMBER_NAME,
                member.detail.profileUrl,
                member.createdAt,
                review.createdAt,
                review.rating,
                review.content))
                .from(review)
                .join(review.reservation, reservation)
                .join(review.member, member)
                .where(reservation.accommodation.id.eq(accommodationId))
                .orderBy(review.createdAt.desc())
                .fetch();
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
                        .where(review.member.id.eq(memberId)),
                countQuery -> countQuery.select(review.count())
                                        .from(review)
                                        .where(review.member.id.eq(memberId))
        );
    }
}
