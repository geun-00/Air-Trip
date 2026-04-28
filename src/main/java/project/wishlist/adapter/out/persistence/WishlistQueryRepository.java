package project.wishlist.adapter.out.persistence;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.stereotype.Repository;
import project.accommodation.adapter.out.persistence.model.AccAllImagesQueryDto;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.wishlist.adapter.in.web.response.WishlistsResponse;
import project.wishlist.adapter.out.persistence.model.WishlistDetailQueryDto;
import project.wishlist.adapter.out.persistence.model.WishlistInfoRow;
import project.wishlist.domain.QWishlistAccommodation;
import project.wishlist.domain.Wishlist;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.constructor;
import static project.accommodation.domain.QAccommodation.accommodation;
import static project.accommodation.domain.QAccommodationImage.accommodationImage;
import static project.member.domain.QMember.member;
import static project.reservation.domain.QReservation.reservation;
import static project.review.domain.QReview.review;
import static project.wishlist.domain.QWishlist.wishlist;
import static project.wishlist.domain.QWishlistAccommodation.wishlistAccommodation;

@Repository
public class WishlistQueryRepository extends CustomQuerydslRepositorySupport {

    private static final NumberExpression<Double> REVIEW_RATING = Expressions.numberTemplate(Double.class, "{0}", review.rating);

    public WishlistQueryRepository() {
        super(Wishlist.class);
    }

    public boolean existsWishlistAccommodation(Long wishlistId, Long accommodationId, Long memberId) {
        return getQueryFactory()
                .selectOne()
                .from(wishlistAccommodation)
                .join(wishlistAccommodation.wishlist, wishlist)
                .where(wishlistAccommodation.accommodation.id.eq(accommodationId),
                        wishlistAccommodation.wishlist.id.eq(wishlistId),
                        wishlist.member.id.eq(memberId)
                )
                .fetchFirst() != null;
    }

    public List<WishlistDetailQueryDto> findWishlistDetails(Long wishlistId, Long memberId) {
        return select(constructor(WishlistDetailQueryDto.class,
                accommodation.id,
                wishlist.name,
                accommodation.title,
                accommodation.detail.description,
                accommodation.geoPoint.longitude,
                accommodation.geoPoint.latitude,
                REVIEW_RATING.avg().coalesce(0.0),
                wishlistAccommodation.memo
        ))
                .from(wishlistAccommodation)
                .join(wishlistAccommodation.wishlist, wishlist)
                .join(wishlistAccommodation.accommodation, accommodation)
                .leftJoin(reservation).on(reservation.accommodationId.eq(accommodation.id))
                .leftJoin(review).on(review.reservationId.eq(reservation.id))
                .where(wishlist.id.eq(wishlistId),
                        wishlist.member.id.eq(memberId)
                )
                .groupBy(
                        accommodation.id,
                        wishlist.name,
                        accommodation.title,
                        accommodation.detail.description,
                        accommodation.geoPoint.longitude,
                        accommodation.geoPoint.latitude,
                        wishlistAccommodation.memo
                )
                .fetch();
    }

    public List<AccAllImagesQueryDto> findAllImages(List<Long> accIds) {
        return select(constructor(AccAllImagesQueryDto.class,
                accommodationImage.accommodation.id,
                accommodationImage.imageUrl))
                .from(accommodationImage)
                .where(accommodationImage.accommodation.id.in(accIds))
                .fetch();
    }

    public List<WishlistsResponse> getAllWishlists(Long memberId) {
        QWishlistAccommodation waSub = new QWishlistAccommodation("waSub");
        JPQLQuery<Long> recentAccIdSubquery = JPAExpressions.select(waSub.accommodation.id)
                                                            .from(waSub)
                                                            .where(waSub.wishlist.eq(wishlist)
                                                                                 .and(waSub.id.eq(
                                                                                         JPAExpressions.select(waSub.id.max())
                                                                                                       .from(waSub)
                                                                                                       .where(waSub.wishlist.eq(wishlist))
                                                                                 )));

        return select(constructor(
                WishlistsResponse.class,
                wishlist.id,
                wishlist.name,
                accommodationImage.imageUrl,
                wishlistAccommodation.accommodation.count().intValue().coalesce(0)))
                .from(wishlist)
                .join(wishlist.member, member)
                .leftJoin(wishlistAccommodation).on(wishlistAccommodation.wishlist.eq(wishlist))
                .leftJoin(accommodation).on(accommodation.id.eq(recentAccIdSubquery))
                .leftJoin(accommodationImage)
                .on(accommodationImage.accommodation.eq(accommodation).and(accommodationImage.thumbnail.isTrue()))
                .where(member.id.eq(memberId))
                .groupBy(wishlist.id, wishlist.name, accommodationImage.imageUrl)
                .fetch();
    }

    public Optional<WishlistInfoRow> getWishlistInfo(Long accId, Long memberId) {
        return Optional.ofNullable(
                select(constructor(
                        WishlistInfoRow.class,
                        wishlistAccommodation.accommodation.id,
                        wishlistAccommodation.isNotNull(),
                        wishlist.id,
                        wishlist.name))
                        .from(wishlist)
                        .leftJoin(wishlistAccommodation).on(
                                wishlistAccommodation.wishlist.eq(wishlist),
                                wishlistAccommodation.accommodation.id.eq(accId)
                        )
                        .where(wishlist.member.id.eq(memberId))
                        .fetchOne()
        );
    }

    public List<WishlistInfoRow> getWishlistInfos(List<Long> accommodationIds, Long memberId) {
        if (accommodationIds == null || accommodationIds.isEmpty() || memberId == null) {
            return Collections.emptyList();
        }

        return select(
                constructor(WishlistInfoRow.class,
                wishlistAccommodation.accommodation.id,
                wishlistAccommodation.isNotNull(),
                wishlist.id,
                wishlist.name))
                .from(wishlist)
                .join(wishlistAccommodation).on(wishlistAccommodation.wishlist.eq(wishlist))
                .where(
                        wishlist.member.id.eq(memberId),
                        wishlistAccommodation.accommodation.id.in(accommodationIds)
                )
                .fetch();
    }
}
