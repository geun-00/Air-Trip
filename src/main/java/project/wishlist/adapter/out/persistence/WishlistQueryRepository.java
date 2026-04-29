package project.wishlist.adapter.out.persistence;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.stereotype.Repository;
import project.accommodation.adapter.out.persistence.model.AccommodationImageRow;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.wishlist.adapter.out.persistence.model.WishlistDetailRow;
import project.wishlist.adapter.out.persistence.model.WishlistInfoRow;
import project.wishlist.adapter.out.persistence.model.WishlistsRow;
import project.wishlist.domain.QWishlistAccommodation;
import project.wishlist.domain.Wishlist;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.constructor;
import static project.accommodation.domain.QAccommodation.accommodation;
import static project.accommodation.domain.QAccommodationImage.accommodationImage;
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

    public List<WishlistDetailRow> findWishlistDetails(Long wishlistId, Long memberId) {
        return select(constructor(
                WishlistDetailRow.class,
                accommodation.id,
                wishlist.name,
                accommodation.title,
                accommodation.detail.description,
                accommodation.geoPoint.longitude,
                accommodation.geoPoint.latitude,
                REVIEW_RATING.avg().coalesce(0.0),
                wishlistAccommodation.memo
        )).from(wishlistAccommodation)
          .join(wishlistAccommodation.wishlist, wishlist)
          .join(accommodation)
          .on(accommodation.id.eq(wishlistAccommodation.accommodationId))
          .leftJoin(reservation)
          .on(reservation.accommodationId.eq(accommodation.id))
          .leftJoin(review)
          .on(review.reservationId.eq(reservation.id))
          .where(
                  wishlist.id.eq(wishlistId),
                  wishlist.memberId.eq(memberId)
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

    public List<AccommodationImageRow> findAllImages(List<Long> accommodationIds) {
        return select(constructor(
                AccommodationImageRow.class,
                accommodationImage.accommodation.id,
                accommodationImage.imageUrl
        )).from(accommodationImage)
          .where(accommodationImage.accommodation.id.in(accommodationIds))
          .fetch();
    }

    public List<WishlistsRow> getAllWishlists(Long memberId) {
        QWishlistAccommodation recent = new QWishlistAccommodation("recent");
        QWishlistAccommodation latest = new QWishlistAccommodation("latest");
        JPQLQuery<Long> recentAccommodationId = JPAExpressions.select(recent.accommodationId)
                                                              .from(recent)
                                                              .where(
                                                                      recent.wishlist.eq(wishlist),
                                                                      recent.id.eq(JPAExpressions.select(latest.id.max())
                                                                                                  .from(latest)
                                                                                                  .where(latest.wishlist.eq(wishlist)))
                                                              );
        return select(constructor(WishlistsRow.class,
                wishlist.id,
                wishlist.name,
                recentAccommodationId,
                wishlistAccommodation.accommodationId.count().intValue().coalesce(0)
        )).from(wishlist)
          .leftJoin(wishlistAccommodation)
          .on(wishlistAccommodation.wishlist.eq(wishlist))
          .where(wishlist.memberId.eq(memberId))
          .groupBy(
                  wishlist.id,
                  wishlist.name
          )
          .fetch();
    }

    public Optional<WishlistInfoRow> getWishlistInfo(Long accId, Long memberId) {
        return Optional.ofNullable(select(constructor(WishlistInfoRow.class,
                wishlistAccommodation.accommodationId,
                wishlistAccommodation.isNotNull(),
                wishlist.id,
                wishlist.name
        )).from(wishlist)
          .leftJoin(wishlistAccommodation)
          .on(
                  wishlistAccommodation.wishlist.eq(wishlist),
                  wishlistAccommodation.accommodationId.eq(accId)
          )
          .where(wishlist.memberId.eq(memberId))
          .fetchOne());
    }

    public List<WishlistInfoRow> getWishlistInfos(List<Long> accommodationIds, Long memberId) {
        if (accommodationIds == null || accommodationIds.isEmpty() || memberId == null) {
            return Collections.emptyList();
        }

        return select(constructor(
                WishlistInfoRow.class,
                wishlistAccommodation.accommodationId,
                wishlistAccommodation.isNotNull(),
                wishlist.id,
                wishlist.name
        )).from(wishlist)
          .join(wishlistAccommodation)
          .on(wishlistAccommodation.wishlist.eq(wishlist))
          .where(
                  wishlist.memberId.eq(memberId),
                  wishlistAccommodation.accommodationId.in(accommodationIds)
          )
          .fetch();
    }
}
