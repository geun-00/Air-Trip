package project.review.adapter.out.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.review.adapter.out.persistence.model.DetailReviewRow;
import project.review.adapter.out.persistence.model.MyReviewRow;
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
                Expressions.constant(accommodationId),
                member.id,
                MEMBER_NAME,
                member.detail.profileUrl,
                member.createdAt,
                review.createdAt,
                review.rating,
                review.content))
                .from(review)
                .join(reservation).on(review.reservationId.eq(reservation.id))
                .join(member).on(review.memberId.eq(member.id))
                .where(reservation.accommodationId.eq(accommodationId))
                .orderBy(review.createdAt.desc())
                .fetch();
    }

    public List<DetailReviewRow> findReviewsByAccommodationIdIn(List<Long> accommodationIds) {
        return select(constructor(DetailReviewRow.class,
                reservation.accommodationId,
                member.id,
                MEMBER_NAME,
                member.detail.profileUrl,
                member.createdAt,
                review.createdAt,
                review.rating,
                review.content))
                .from(review)
                .join(reservation).on(review.reservationId.eq(reservation.id))
                .join(member).on(review.memberId.eq(member.id))
                .where(reservation.accommodationId.in(accommodationIds))
                .orderBy(review.createdAt.desc())
                .fetch();
    }

    public Page<MyReviewRow> getMyReviews(Long memberId, Pageable pageable) {
        return applyPagination(pageable,
                contentQuery -> contentQuery
                        .select(Projections.constructor(
                                MyReviewRow.class,
                                review.id,
                                accommodation.id,
                                accommodationImage.imageUrl,
                                accommodation.title,
                                review.content,
                                review.rating,
                                review.createdAt
                        ))
                        .from(review)
                        .join(reservation).on(review.reservationId.eq(reservation.id))
                        .join(accommodation).on(accommodation.id.eq(reservation.accommodationId))
                        .join(accommodationImage)
                        .on(accommodationImage.accommodation.eq(accommodation)
                                                            .and(accommodationImage.thumbnail.isTrue()))
                        .where(review.memberId.eq(memberId)),
                countQuery -> countQuery.select(review.count())
                                        .from(review)
                                        .where(review.memberId.eq(memberId))
        );
    }
}
