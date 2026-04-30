package project.accommodation.adapter.out.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import project.accommodation.adapter.out.persistence.model.AccommodationImagesRow;
import project.accommodation.adapter.out.persistence.model.DetailAccommodationRow;
import project.accommodation.adapter.out.persistence.model.FilteredAccommodationRow;
import project.accommodation.adapter.out.persistence.model.GuestDetailAccommodationRow;
import project.accommodation.adapter.out.persistence.model.GuestFilteredAccommodationRow;
import project.accommodation.adapter.out.persistence.model.GuestMainAccommodationRow;
import project.accommodation.adapter.out.persistence.model.MainAccommodationRow;
import project.accommodation.adapter.out.persistence.model.WishlistRow;
import project.accommodation.application.out.query.model.SearchAccommodationsCondition;
import project.accommodation.domain.Accommodation;
import project.area.domain.QAreaCode;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.common.domain.StayDatePolicy;
import project.wishlist.domain.WishlistName;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.querydsl.core.types.Projections.constructor;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.StringUtils.hasText;
import static project.accommodation.adapter.out.persistence.QAccommodationStats.accommodationStats;
import static project.accommodation.domain.QAccommodation.accommodation;
import static project.accommodation.domain.QAccommodationAmenity.accommodationAmenity;
import static project.accommodation.domain.QAccommodationImage.accommodationImage;
import static project.accommodation.domain.QAccommodationPrice.accommodationPrice;
import static project.amenity.domain.QAmenity.amenity;
import static project.reservation.domain.QReservation.reservation;
import static project.review.domain.QReview.review;
import static project.wishlist.domain.QWishlist.wishlist;
import static project.wishlist.domain.QWishlistAccommodation.wishlistAccommodation;

@Repository
public class AccommodationQueryRepository extends CustomQuerydslRepositorySupport {

    private static final NumberExpression<Double> REVIEW_RATING = Expressions.numberTemplate(Double.class, "{0}", review.rating);
    private static final QAreaCode childAreaCode = new QAreaCode("childAreaCode");
    private static final QAreaCode parentAreaCode = new QAreaCode("parentAreaCode");

    public AccommodationQueryRepository() {
        super(Accommodation.class);
    }

    public List<MainAccommodationRow> getAreaAccommodations(
            StayDatePolicy stayDatePolicy,
            Long memberId
    ) {
        List<MainAccommodationRow> rows = select(constructor(GuestMainAccommodationRow.class,
                accommodationStats.accommodationId,
                accommodationStats.title,
                accommodationPrice.price,
                accommodationStats.averageRating,
                accommodationStats.thumbnailUrl,
                accommodationStats.reservationCount,
                accommodationStats.areaName,
                accommodationStats.areaCode
        ))
                .from(accommodationStats)
                .join(accommodationPrice)
                .on(accommodationPrice.accommodation.id.eq(accommodationStats.accommodationId)
                        .and(accommodationPrice.season.eq(stayDatePolicy.season()))
                        .and(accommodationPrice.dayType.eq(stayDatePolicy.dayType())))
                .fetch()
                .stream()
                .map(this::toMainRow)
                .toList();

        if (memberId == null) {
            return rows;
        }

        List<Long> accommodationIds = rows.stream()
                                          .map(MainAccommodationRow::accommodationId)
                                          .toList();
        Map<Long, WishlistRow> wishlistMap = fetchWishlistMap(memberId, accommodationIds);

        return rows.stream()
                   .map(row -> {
                       WishlistRow info = wishlistMap.get(row.accommodationId());
                       if (info == null) {
                           return row;
                       }
                       return new MainAccommodationRow(
                               row.accommodationId(),
                               row.title(),
                               row.price(),
                               row.avgRate(),
                               row.thumbnailUrl(),
                               true,
                               info.wishlistId(),
                               info.wishlistName(),
                               row.reservationCount(),
                               row.areaName(),
                               row.areaCode()
                       );
                   })
                   .toList();
    }

    private MainAccommodationRow toMainRow(GuestMainAccommodationRow row) {
        return new MainAccommodationRow(
                row.accommodationId(),
                row.title(),
                row.price(),
                row.avgRate(),
                row.thumbnailUrl(),
                false,
                null,
                null,
                row.reservationCount(),
                row.areaName(),
                row.areaCode()
        );
    }

    public Page<FilteredAccommodationRow> getFilteredPagingAccommodations(SearchAccommodationsCondition condition) {
        Pageable pageable = condition.pageable();

        List<GuestFilteredAccommodationRow> fetched = select(constructor(GuestFilteredAccommodationRow.class,
                accommodation.id,
                accommodation.title,
                accommodationPrice.price,
                REVIEW_RATING.avg().coalesce(0.0),
                review.count().intValue().coalesce(0)
        ))
                .from(accommodation)
                .join(accommodationPrice).on(
                        accommodationPrice.accommodation.eq(accommodation)
                                .and(accommodationPrice.season.eq(condition.stayDatePolicy().season()))
                                .and(accommodationPrice.dayType.eq(condition.stayDatePolicy().dayType())))
                .join(accommodationImage).on(accommodationImage.accommodation.eq(accommodation))
                .join(childAreaCode).on(childAreaCode.code.eq(accommodation.areaCode))
                .leftJoin(childAreaCode.parent, parentAreaCode)
                .leftJoin(reservation).on(reservation.accommodationId.eq(accommodation.id))
                .leftJoin(review).on(review.reservationId.eq(reservation.id))
                .where(
                        eqAreaCode(condition.areaCode()),
                        goePrice(condition.priceGoe()),
                        loePrice(condition.priceLoe()),
                        hasAllAmenities(condition.amenities())
                )
                .groupBy(accommodation.id, accommodation.title, accommodationPrice.price)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> accommodationIds = fetched.stream()
                                             .map(GuestFilteredAccommodationRow::accommodationId)
                                             .toList();
        Map<Long, List<String>> imagesMap = fetchImagesMap(accommodationIds);

        if (condition.memberId() == null) {
            List<FilteredAccommodationRow> content = fetched.stream()
                    .map(row -> toFilteredRow(
                            row,
                            imagesMap.getOrDefault(row.accommodationId(), List.of())
                    ))
                    .toList();

            return PageableExecutionUtils.getPage(content, pageable, countQuery(condition)::fetchOne);
        }

        Map<Long, WishlistRow> wishlistMap = fetchWishlistMap(condition.memberId(), accommodationIds);

        List<FilteredAccommodationRow> content = fetched.stream()
                .map(row -> {
                    List<String> imageUrls = imagesMap.getOrDefault(row.accommodationId(), List.of());
                    WishlistRow info = wishlistMap.get(row.accommodationId());
                    if (info == null) {
                        return toFilteredRow(row, imageUrls);
                    }
                    return new FilteredAccommodationRow(
                            row.accommodationId(),
                            row.title(),
                            row.price(),
                            row.avgRate(),
                            row.reviewCount(),
                            imageUrls,
                            true,
                            info.wishlistId(),
                            info.wishlistName()
                    );
                })
                .toList();

        return PageableExecutionUtils.getPage(content, pageable, countQuery(condition)::fetchOne);
    }

    private FilteredAccommodationRow toFilteredRow(
            GuestFilteredAccommodationRow row,
            List<String> imageUrls
    ) {
        return new FilteredAccommodationRow(
                row.accommodationId(),
                row.title(),
                row.price(),
                row.avgRate(),
                row.reviewCount(),
                imageUrls,
                false,
                null,
                null
        );
    }

    private Map<Long, List<String>> fetchImagesMap(List<Long> accommodationIds) {
        List<AccommodationImagesRow> imageRows = select(constructor(AccommodationImagesRow.class,
                accommodationImage.accommodation.id,
                accommodationImage.imageUrl
        ))
                .from(accommodationImage)
                .where(accommodationImage.accommodation.id.in(accommodationIds))
                .orderBy(accommodationImage.id.desc())
                .fetch();

        return imageRows.stream()
                .collect(groupingBy(
                        AccommodationImagesRow::accommodationId,
                        mapping(
                                AccommodationImagesRow::imageUrl,
                                collectingAndThen(toList(), list -> list.stream().limit(10).toList())
                        )
                ));
    }

    private JPAQuery<Long> countQuery(SearchAccommodationsCondition condition) {
        return select(accommodation.count())
                .from(accommodation)
                .join(accommodationPrice)
                .on(accommodationPrice.accommodation.eq(accommodation)
                        .and(accommodationPrice.season.eq(condition.stayDatePolicy().season()))
                        .and(accommodationPrice.dayType.eq(condition.stayDatePolicy().dayType())))
                .join(childAreaCode).on(childAreaCode.code.eq(accommodation.areaCode))
                .leftJoin(childAreaCode.parent, parentAreaCode)
                .where(
                        eqAreaCode(condition.areaCode()),
                        goePrice(condition.priceGoe()),
                        loePrice(condition.priceLoe()),
                        hasAllAmenities(condition.amenities())
                );
    }

    public List<DetailAccommodationRow> findAccommodations(
            List<Long> accommodationIds,
            StayDatePolicy stayDatePolicy
    ) {
        return select(constructor(DetailAccommodationRow.class,
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
                                  Expressions.constant(false),
                                  Expressions.nullExpression(Long.class),
                                  Expressions.nullExpression(WishlistName.class),
                                  JPAExpressions.select(REVIEW_RATING.avg().coalesce(0.0))
                              .from(review)
                              .join(reservation).on(review.reservationId.eq(reservation.id))
                              .where(reservation.accommodationId.eq(accommodation.id))
        ))
                .from(accommodation)
                .join(accommodationPrice).on(
                        accommodationPrice.accommodation.eq(accommodation)
                                .and(accommodationPrice.season.eq(stayDatePolicy.season()))
                                .and(accommodationPrice.dayType.eq(stayDatePolicy.dayType())))
                .where(accommodation.id.in(accommodationIds))
                .fetch();
    }

    public Optional<DetailAccommodationRow> findAccommodation(
            Long accommodationId,
            Long memberId,
            StayDatePolicy stayDatePolicy
    ) {
        JPQLQuery<Double> avgRateSubquery = JPAExpressions.select(REVIEW_RATING.avg().coalesce(0.0))
                                                          .from(review)
                                                          .join(reservation).on(review.reservationId.eq(reservation.id))
                                                          .where(reservation.accommodationId.eq(accommodationId));

        Optional<GuestDetailAccommodationRow> fetched = Optional.ofNullable(
                select(constructor(GuestDetailAccommodationRow.class,
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
                ))
                        .from(accommodation)
                        .join(accommodationPrice).on(
                                accommodationPrice.accommodation.eq(accommodation)
                                        .and(accommodationPrice.season.eq(stayDatePolicy.season()))
                                        .and(accommodationPrice.dayType.eq(stayDatePolicy.dayType())))
                        .where(accommodation.id.eq(accommodationId))
                        .fetchOne()
        );

        if (fetched.isEmpty() || memberId == null) {
            return fetched.map(row -> toDetailRow(row, null));
        }

        WishlistRow wishlistInfo = fetchWishlistInfo(memberId, accommodationId);

        return fetched.map(row -> toDetailRow(row, wishlistInfo));
    }

    private DetailAccommodationRow toDetailRow(
            GuestDetailAccommodationRow accommodationRow,
            WishlistRow wishlistRow
    ) {
        boolean isInWishlist = (wishlistRow != null);

        return new DetailAccommodationRow(
                accommodationRow.accommodationId(),
                accommodationRow.title(),
                accommodationRow.capacity(),
                accommodationRow.address(),
                accommodationRow.mapX(),
                accommodationRow.mapY(),
                accommodationRow.checkIn(),
                accommodationRow.checkOut(),
                accommodationRow.description(),
                accommodationRow.number(),
                accommodationRow.refundRegulation(),
                accommodationRow.price(),
                isInWishlist,
                isInWishlist ? wishlistRow.wishlistId() : null,
                isInWishlist ? wishlistRow.wishlistName() : null,
                accommodationRow.avgRate()
        );
    }

    private WishlistRow fetchWishlistInfo(Long memberId, Long accommodationId) {
        return fetchWishlistMap(memberId, List.of(accommodationId))
                .get(accommodationId);
    }

    private Map<Long, WishlistRow> fetchWishlistMap(Long memberId, List<Long> accommodationIds) {
        List<WishlistRow> rows = select(constructor(WishlistRow.class,
                wishlistAccommodation.accommodationId,
                wishlist.id,
                wishlist.name
        )).from(wishlistAccommodation)
          .join(wishlistAccommodation.wishlist, wishlist)
          .where(wishlist.memberId.eq(memberId), wishlistAccommodation.accommodationId.in(accommodationIds))
          .fetch();

        return rows.stream()
                   .collect(toMap(
                           WishlistRow::accommodationId,
                           wishlistRow -> wishlistRow
                   ));
    }

    private BooleanExpression eqAreaCode(String code) {
        return hasText(code) ? parentAreaCode.code.eq(code) : null;
    }

    private BooleanExpression goePrice(Integer price) {
        return (price != null) ? accommodationPrice.price.goe(price) : null;
    }

    private BooleanExpression loePrice(Integer price) {
        return (price != null) ? accommodationPrice.price.loe(price) : null;
    }

    private BooleanExpression hasAllAmenities(List<String> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return null;
        }

        return JPAExpressions
                .select(accommodationAmenity.amenityId.countDistinct())
                .from(accommodationAmenity)
                .join(amenity).on(amenity.id.eq(accommodationAmenity.amenityId))
                .where(accommodationAmenity.accommodation.eq(accommodation),
                        amenity.name.in(amenities))
                .eq((long) amenities.size());
    }
}
