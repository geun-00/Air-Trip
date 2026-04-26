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
import project.accommodation.adapter.in.web.request.AccommodationSearchCondition;
import project.accommodation.adapter.in.web.request.ViewHistoryDto;
import project.accommodation.adapter.in.web.response.AccommodationCommonInfo.DetailReviewDto;
import project.accommodation.adapter.in.web.response.FilteredAccListResDto;
import project.accommodation.adapter.out.persistence.model.AccAllImagesQueryDto;
import project.accommodation.adapter.out.persistence.model.DetailAccommodationQueryDto;
import project.accommodation.adapter.out.persistence.model.FilteredAccListQueryDto;
import project.accommodation.adapter.out.persistence.model.ImageDataQueryDto;
import project.accommodation.adapter.out.persistence.model.MainAccListQueryDto;
import project.accommodation.domain.Accommodation;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.common.domain.DayType;
import project.common.domain.Season;

import java.time.LocalDateTime;
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
import static project.history.domain.QViewHistory.viewHistory;
import static project.member.domain.QMember.member;
import static project.reservation.domain.QReservation.reservation;
import static project.review.domain.QReview.review;
import static project.wishlist.domain.QWishlist.wishlist;
import static project.wishlist.domain.QWishlistAccommodation.wishlistAccommodation;

@Repository
public class AccommodationQueryRepository extends CustomQuerydslRepositorySupport {

    private static final StringPath MEMBER_NAME = Expressions.stringPath(member, "name");

    public AccommodationQueryRepository() {
        super(Accommodation.class);
    }

    public List<MainAccListQueryDto> getAreaAccommodations(Season season, DayType dayType, Long memberId) {
        return new AccommodationQueryBuilder(getQueryFactory(), dayType, season, memberId)
                .fetchMainAccList();
    }

    public Page<FilteredAccListResDto> getFilteredPagingAccommodations(
            AccommodationSearchCondition searchDto,
            Long memberId, Pageable pageable,
            Season season, DayType dayType) {
        //이미지 목록 제외 필드 조회
        List<FilteredAccListQueryDto> queryDtos = new AccommodationQueryBuilder(getQueryFactory(), dayType, season, memberId)
                .fetchFilteredAccList(pageable,
                        eqAreaCode(searchDto.areaCode()),
                        goePrice(searchDto.priceGoe()),
                        loePrice(searchDto.priceLoe()),
                        hasAllAmenities(searchDto.amenities())
                );

        //in절로 조회된 숙소의 이미지 목록 조회(전체)
        List<Long> accIds = queryDtos.stream().map(FilteredAccListQueryDto::accommodationId).toList();
        List<AccAllImagesQueryDto> imagesQueryDtos = select(constructor(AccAllImagesQueryDto.class,
                                                                        accommodationImage.accommodation.id, accommodationImage.imageUrl))
                .from(accommodationImage)
                .where(accommodationImage.accommodation.id.in(accIds))
                .orderBy(accommodationImage.id.desc())
                .fetch();

        //직접 숙소당 최대 10개 이미지 목록 매핑
        Map<Long, List<String>> imagesMap = imagesQueryDtos.stream()
                                                           .collect(groupingBy(
                                                                   AccAllImagesQueryDto::accommodationId,
                                                                   mapping(
                                                                           AccAllImagesQueryDto::imageUrl,
                                                                           collectingAndThen(toList(), list -> list.stream()
                                                                                                                   .limit(10)
                                                                                                                   .toList())
                                                                   )
                                                           ));
        //응답 DTO 매핑
        List<FilteredAccListResDto> content = queryDtos.stream()
                                                       .map(dto -> FilteredAccListResDto.from(dto, imagesMap.getOrDefault(dto.accommodationId(), List.of())))
                                                       .toList();

        //카운트쿼리
        JPAQuery<Long> countQuery = select(accommodation.count())
                .from(accommodation)
                .join(accommodationPrice)
                .on(accommodationPrice.accommodation.eq(accommodation)
                                                    .and(accommodationPrice.season.eq(season))
                                                    .and(accommodationPrice.dayType.eq(dayType)))
                .join(sigunguCode).on(sigunguCode.code.eq(accommodation.sigunguCode))
                .join(sigunguCode.areaCode, areaCode)
                .where(
                        eqAreaCode(searchDto.areaCode()),
                        goePrice(searchDto.priceGoe()),
                        loePrice(searchDto.priceLoe()),
                        hasAllAmenities(searchDto.amenities())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    public Optional<DetailAccommodationQueryDto> findAccommodation(Long accId, Long memberId, Season season, DayType dayType) {
        return new AccommodationQueryBuilder(getQueryFactory(), dayType, season, memberId)
                .fetchDetailAcc(accId);
    }

    public List<ImageDataQueryDto> findImages(Long accId) {
        return select(constructor(
                ImageDataQueryDto.class,
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

    public List<DetailReviewDto> findReviews(Long accId) {
        return select(constructor(
                DetailReviewDto.class,
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

    public List<ViewHistoryDto> findViewHistories(Long memberId) {
        return select(constructor(
                ViewHistoryDto.class,
                viewHistory.viewedAt,
                accommodation.id,
                accommodation.title,
                review.rating.avg().coalesce(0.0),
                accommodationImage.imageUrl,
                wishlist.isNotNull(),
                wishlist.id,
                wishlist.name))
                .from(viewHistory)
                .join(viewHistory.accommodation, accommodation)

                .join(accommodationImage).on(accommodationImage.accommodation.eq(accommodation)
                                                                             .and(accommodationImage.thumbnail.isTrue()))

                .leftJoin(wishlistAccommodation).on(wishlistAccommodation.accommodation.eq(accommodation))
                .leftJoin(wishlistAccommodation.wishlist, wishlist).on(wishlist.member.id.eq(memberId))

                .leftJoin(reservation).on(reservation.accommodation.eq(accommodation))
                .leftJoin(review).on(review.reservation.eq(reservation))

                .where(viewHistory.member.id.eq(memberId)
                                           .and(viewHistory.viewedAt.after(LocalDateTime.now().minusDays(30))))

                .groupBy(viewHistory.viewedAt, accommodation.id, accommodation.title, accommodationImage.imageUrl, wishlist.id, wishlist.name)
                .orderBy(viewHistory.viewedAt.desc())
                .fetch();
    }

    public Integer getAccommodationPrice(Long accId, Season season, DayType dayType) {
        return select(accommodationPrice.price)
                .from(accommodationPrice)
                .where(accommodationPrice.accommodation.id.eq(accId)
                                                          .and(accommodationPrice.season.eq(season))
                                                          .and(accommodationPrice.dayType.eq(dayType)))
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
