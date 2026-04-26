package project.accommodation.adapter.out.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import project.accommodation.adapter.out.persistence.model.AccAllImageRow;
import project.accommodation.adapter.out.persistence.model.DetailAccommodationRow;
import project.accommodation.adapter.out.persistence.model.DetailReviewRow;
import project.accommodation.adapter.out.persistence.model.FilteredAccommodationRow;
import project.accommodation.adapter.out.persistence.model.ImageDataRow;
import project.accommodation.adapter.out.persistence.model.MainAccommodationRow;
import project.accommodation.application.out.query.SearchAccommodationsCondition;
import project.accommodation.domain.Accommodation;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.common.domain.StayDatePolicy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.querydsl.core.types.Projections.constructor;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;
import static project.accommodation.domain.QAccommodation.accommodation;
import static project.accommodation.domain.QAccommodationAmenity.accommodationAmenity;
import static project.accommodation.domain.QAccommodationImage.accommodationImage;
import static project.accommodation.domain.QAccommodationPrice.accommodationPrice;
import static project.amenity.domain.QAmenity.amenity;
import static project.area.domain.QAreaCode.areaCode;
import static project.area.domain.QSigunguCode.sigunguCode;
import static project.member.domain.QMember.member;
import static project.reservation.domain.QReservation.reservation;
import static project.review.domain.QReview.review;

@Repository
public class AccommodationQueryRepository extends CustomQuerydslRepositorySupport {

    private static final StringPath MEMBER_NAME = Expressions.stringPath(member, "name");

    public AccommodationQueryRepository() {
        super(Accommodation.class);
    }

    public List<MainAccommodationRow> getAreaAccommodations(
            StayDatePolicy stayDatePolicy,
            Long memberId
    ) {
        return new AccommodationQueryBuilder(getQueryFactory(), stayDatePolicy, memberId)
                .fetchMainAccommodations();
    }

    public Page<FilteredAccommodationRow> getFilteredPagingAccommodations(
            SearchAccommodationsCondition condition,
            Pageable pageable
    ) {
        //이미지 목록 제외 필드 조회
        List<FilteredAccommodationRow> filteredAccommodations = new AccommodationQueryBuilder(
                getQueryFactory(),
                condition.stayDatePolicy(),
                condition.memberId()
        )
                .fetchFilteredAccList(pageable,
                        eqAreaCode(condition.areaCode()),
                        goePrice(condition.priceGoe()),
                        loePrice(condition.priceLoe()),
                        hasAllAmenities(condition.amenities())
                );

        //in절로 조회된 숙소의 이미지 목록 조회(전체)
        List<Long> accIds = filteredAccommodations.stream().map(FilteredAccommodationRow::accommodationId).toList();
        List<AccAllImageRow> imageRows = select(constructor(AccAllImageRow.class,
                                                            accommodationImage.accommodation.id, accommodationImage.imageUrl))
                .from(accommodationImage)
                .where(accommodationImage.accommodation.id.in(accIds))
                .orderBy(accommodationImage.id.desc())
                .fetch();

        //직접 숙소당 최대 10개 이미지 목록 매핑
        Map<Long, List<String>> imagesMap = imageRows.stream()
                                                     .collect(
                                                             groupingBy(
                                                             AccAllImageRow::accommodationId,
                                                             mapping(
                                                                     AccAllImageRow::imageUrl,
                                                                     collectingAndThen(toList(), list -> list.stream().limit(10).toList())
                                                             )
                                                     ));
        List<FilteredAccommodationRow> content = filteredAccommodations.stream()
                                                     .map(row -> new FilteredAccommodationRow(
                                                             row.accommodationId(),
                                                             row.title(),
                                                             row.price(),
                                                             row.avgRate(),
                                                             row.reviewCount(),
                                                             imagesMap.getOrDefault(row.accommodationId(), List.of()),
                                                             row.isInWishlist(),
                                                             row.wishlistId(),
                                                             row.wishlistName()
                                                     ))
                                                     .toList();

        JPAQuery<Long> countQuery = select(accommodation.count())
                .from(accommodation)
                .join(accommodationPrice)
                .on(accommodationPrice.accommodation.eq(accommodation)
                                                    .and(accommodationPrice.season.eq(condition.stayDatePolicy().season()))
                                                    .and(accommodationPrice.dayType.eq(condition.stayDatePolicy().dayType())))
                .join(sigunguCode).on(sigunguCode.code.eq(accommodation.sigunguCode))
                .join(sigunguCode.areaCode, areaCode)
                .where(
                        eqAreaCode(condition.areaCode()),
                        goePrice(condition.priceGoe()),
                        loePrice(condition.priceLoe()),
                        hasAllAmenities(condition.amenities())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    public Optional<DetailAccommodationRow> findAccommodation(Long accId, Long memberId, StayDatePolicy stayDatePolicy) {
        return new AccommodationQueryBuilder(getQueryFactory(), stayDatePolicy, memberId)
                .fetchDetailAcc(accId);
    }

    public List<ImageDataRow> findImages(Long accId) {
        return select(constructor(
                ImageDataRow.class,
                accommodationImage.thumbnail,
                accommodationImage.imageUrl))
                .from(accommodationImage)
                .where(accommodationImage.accommodation.id.eq(accId))
                .fetch();
    }

    public List<String> findAmenities(Long accId) {
        return select(amenity.description)
                .from(accommodationAmenity)
                .join(amenity).on(amenity.id.eq(accommodationAmenity.amenityId))
                .where(accommodationAmenity.accommodation.id.eq(accId))
                .fetch();
    }

    public List<DetailReviewRow> findReviews(Long accId) {
        return select(constructor(
                DetailReviewRow.class,
                member.id,
                MEMBER_NAME,
                member.detail.profileUrl,
                member.createdAt,
                review.createdAt,
                review.rating,
                review.content))
                .from(reservation)
                .join(review).on(review.reservation.eq(reservation))
                .join(review.member, member)
                .where(reservation.accommodation.id.eq(accId))
                .orderBy(review.createdAt.desc())
                .fetch();
    }

    public Integer getAccommodationPrice(Long accommodationId, StayDatePolicy stayDatePolicy) {
        return select(accommodationPrice.price)
                .from(accommodationPrice)
                .where(accommodationPrice.accommodation.id.eq(accommodationId)
                                                          .and(accommodationPrice.season.eq(stayDatePolicy.season()))
                                                          .and(accommodationPrice.dayType.eq(stayDatePolicy.dayType())))
                .fetchOne();
    }

    private BooleanExpression eqAreaCode(String code) {
        return hasText(code) ? areaCode.code.eq(code) : null;
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
