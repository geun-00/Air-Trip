package project.accommodation.adapter.out.persistence;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import project.accommodation.adapter.out.persistence.model.DetailAccommodationRow;
import project.accommodation.adapter.out.persistence.model.FilteredAccommodationRow;
import project.accommodation.adapter.out.persistence.model.MainAccommodationRow;
import project.common.domain.StayDatePolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.constructor;
import static project.accommodation.domain.QAccommodation.accommodation;
import static project.accommodation.domain.QAccommodationImage.accommodationImage;
import static project.accommodation.domain.QAccommodationPrice.accommodationPrice;
import static project.accommodation.adapter.out.persistence.QAccommodationStats.accommodationStats;
import static project.area.domain.QAreaCode.areaCode;
import static project.area.domain.QSigunguCode.sigunguCode;
import static project.reservation.domain.QReservation.reservation;
import static project.review.domain.QReview.review;
import static project.wishlist.domain.QWishlist.wishlist;
import static project.wishlist.domain.QWishlistAccommodation.wishlistAccommodation;

public record AccommodationQueryBuilder(
        JPAQueryFactory queryFactory,
        StayDatePolicy stayDatePolicy,
        Long memberId
) {

    // =====================================================
    // 공통 메서드
    // =====================================================
    private boolean hasMember() {
        return memberId != null;
    }

    private JPAQuery<?> baseQuery() {
        return queryFactory
                .from(accommodation)
                .join(accommodationPrice).on(
                        accommodationPrice.accommodation.eq(accommodation)
                                                        .and(accommodationPrice.season.eq(stayDatePolicy.season()))
                                                        .and(accommodationPrice.dayType.eq(stayDatePolicy.dayType()))
                );
    }

    private JPAQuery<?> withWishlistJoin(JPAQuery<?> query) {
        if (!hasMember()) {
            return query;
        }

        return query.leftJoin(wishlistAccommodation).on(wishlistAccommodation.accommodation.eq(accommodation))
                    .leftJoin(wishlistAccommodation.wishlist, wishlist).on(wishlist.member.id.eq(memberId));
    }

    // =====================================================
    // 메인 페이지용 쿼리
    // =====================================================
    public List<MainAccommodationRow> fetchMainAccommodations() {
        JPAQuery<?> query = buildAreaAccommodationsBaseQuery();

        return query.select(buildMainAccommodationsProjection())
                    .fetch();
    }

    /**
     * 메인 페이지용 베이스쿼리
     */
    private JPAQuery<?> buildAreaAccommodationsBaseQuery() {
        JPAQuery<?> query = queryFactory.from(accommodationStats)
                                        .join(accommodationPrice)
                                        .on(accommodationPrice.accommodation.id.eq(accommodationStats.accommodationId)
                                                                               .and(accommodationPrice.season.eq(stayDatePolicy.season()))
                                                                               .and(accommodationPrice.dayType.eq(stayDatePolicy.dayType())));
        if (!hasMember()) {
            return query;
        }

        return query.leftJoin(wishlistAccommodation).on(wishlistAccommodation.accommodation.id.eq(accommodationStats.accommodationId))
                    .leftJoin(wishlistAccommodation.wishlist, wishlist).on(wishlist.member.id.eq(memberId));
    }

    /**
     * 메인 페이지용 select
     */
    private Expression<MainAccommodationRow> buildMainAccommodationsProjection() {
        if (!hasMember()) {
            return constructor(MainAccommodationRow.class,
                    accommodationStats.accommodationId,
                    accommodationStats.title,
                    accommodationPrice.price,
                    accommodationStats.averageRating,
                    accommodationStats.thumbnailUrl,
                    accommodationStats.reservationCount,
                    accommodationStats.areaName,
                    accommodationStats.areaCode
            );
        }

        return constructor(MainAccommodationRow.class,
                accommodationStats.accommodationId,
                accommodationStats.title,
                accommodationPrice.price,
                accommodationStats.averageRating,
                accommodationStats.thumbnailUrl,
                wishlist.isNotNull(),
                wishlist.id,
                wishlist.name,
                accommodationStats.reservationCount,
                accommodationStats.areaName,
                accommodationStats.areaCode
        );
    }

    // =====================================================
    // 검색 페이지용 쿼리
    // =====================================================
    public List<FilteredAccommodationRow> fetchFilteredAccList(Pageable pageable, BooleanExpression... params) {
        JPAQuery<?> query = withWishlistJoin(buildFilteredBaseQuery());

        return query.select(buildFilteredProjection())
                    .where(params)
                    .groupBy(filteredGroupBy())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
    }

    /**
     * 검색 페이지용 베이스쿼리
     */
    public JPAQuery<?> buildFilteredBaseQuery() {
        return baseQuery()
                .join(accommodationImage).on(accommodationImage.accommodation.eq(accommodation))
                .join(sigunguCode).on(sigunguCode.code.eq(accommodation.sigunguCode))
                .join(sigunguCode.areaCode, areaCode)
                .leftJoin(reservation).on(reservation.accommodation.eq(accommodation))
                .leftJoin(review).on(review.reservation.eq(reservation));
    }

    /**
     * 검색 페이지용 Select
     */
    public Expression<FilteredAccommodationRow> buildFilteredProjection() {
        if (!hasMember()) {
            return constructor(FilteredAccommodationRow.class,
                    accommodation.id,
                    accommodation.title,
                    accommodationPrice.price,
                    review.rating.avg().coalesce(0.0),
                    review.count().intValue().coalesce(0)
            );
        }

        return constructor(FilteredAccommodationRow.class,
                accommodation.id,
                accommodation.title,
                accommodationPrice.price,
                review.rating.avg().coalesce(0.0),
                review.count().intValue().coalesce(0),
                wishlist.isNotNull(),
                wishlist.id,
                wishlist.name
        );
    }

    /**
     * 검색 페이지용 groupBy
     */
    private Expression<?>[] filteredGroupBy() {
        List<Expression<?>> fields = new ArrayList<>(List.of(
                accommodation.id,
                accommodation.title,
                accommodationPrice.price
        ));

        if (hasMember()) {
            fields.add(wishlist.id);
            fields.add(wishlist.name);
        }

        return fields.toArray(new Expression[0]);
    }

    // =====================================================
    // 상세 페이지용 쿼리
    // =====================================================
    public Optional<DetailAccommodationRow> fetchDetailAcc(Long accId) {
        JPAQuery<?> query = baseQuery();

        if (hasMember()) {
            query
                    .leftJoin(wishlist).on(wishlist.member.id.eq(memberId))
                    .leftJoin(wishlistAccommodation).on(
                            wishlistAccommodation.wishlist.eq(wishlist)
                                                          .and(wishlistAccommodation.accommodation.eq(accommodation))
                    );
        }

        return Optional.ofNullable(
                query.select(buildDetailProjection(accId))
                     .where(accommodation.id.eq(accId))
                     .fetchOne()
        );
    }

    /**
     * 상세 페이지용 Select절
     */
    public Expression<DetailAccommodationRow> buildDetailProjection(Long accId) {
        JPQLQuery<Double> avgRateSubquery = JPAExpressions.select(review.rating.avg().coalesce(0.0))
                                                          .from(review)
                                                          .join(review.reservation, reservation)
                                                          .where(reservation.accommodation.id.eq(accId));
        if (!hasMember()) {
            return constructor(DetailAccommodationRow.class,
                    accommodation.id,
                    accommodation.title,
                    accommodation.detail.maxPeople,
                    accommodation.address,
                    accommodation.geoPoint.longitude,
                    accommodation.geoPoint.latitude,
                    accommodation.detail.stayTimePolicy.checkIn,
                    accommodation.detail.stayTimePolicy.checkOut,
                    accommodation.detail.description,
                    accommodation.detail.number,
                    accommodation.detail.refundRegulation,
                    accommodationPrice.price,
                    avgRateSubquery
            );
        }

        return constructor(DetailAccommodationRow.class,
                accommodation.id,
                accommodation.title,
                accommodation.detail.maxPeople,
                accommodation.address,
                accommodation.geoPoint.longitude,
                accommodation.geoPoint.latitude,
                accommodation.detail.stayTimePolicy.checkIn,
                accommodation.detail.stayTimePolicy.checkOut,
                accommodation.detail.description,
                accommodation.detail.number,
                accommodation.detail.refundRegulation,
                accommodationPrice.price,
                wishlist.isNotNull(),
                wishlist.id,
                wishlist.name,
                avgRateSubquery
        );
    }
}
