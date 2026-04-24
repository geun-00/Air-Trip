package project.member.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.member.adapter.in.web.response.ChatMemberSearchResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;
import project.member.adapter.out.persistence.model.DefaultProfileQueryDto;
import project.member.domain.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.constructor;
import static project.accommodation.domain.QAccommodation.accommodation;
import static project.accommodation.domain.QAccommodationImage.accommodationImage;
import static project.member.domain.QMember.member;
import static project.reservation.domain.QReservation.reservation;
import static project.review.domain.QReview.review;

@Repository
public class MemberQueryRepository extends CustomQuerydslRepositorySupport {

    public MemberQueryRepository() {
        super(Member.class);
    }

    public Optional<DefaultProfileQueryDto> getDefaultProfile(Long memberId) {
        return Optional.ofNullable(
                select(constructor(
                        DefaultProfileQueryDto.class,
                        member.name,
                        member.profileUrl,
                        member.createdAt,
                        member.aboutMe,
                        member.isEmailVerified))
                        .from(member)
                        .where(member.id.eq(memberId))
                        .fetchOne()
        );
    }

    public List<ChatMemberSearchResponse> findMembersByName(String name) {
        return select(constructor(
                ChatMemberSearchResponse.class,
                member.id,
                member.name,
                member.createdAt,
                member.profileUrl))
                .from(member)
                .where(member.name.contains(name))
                .fetch();
    }

    public Page<TripHistoryResponse> getTripsHistory(Long memberId, Pageable pageable) {
        return applyPagination(pageable,
                contentQuery ->
                        contentQuery.select(constructor(
                                            TripHistoryResponse.class,
                                            reservation.id,
                                            accommodation.id,
                                            accommodationImage.imageUrl,
                                            accommodation.title,
                                            reservation.startDate,
                                            reservation.endDate,
                                            review.isNotNull()))
                                    .from(accommodation)
                                    .join(accommodationImage)
                                    .on(accommodationImage.accommodation.eq(accommodation)
                                                                        .and(accommodationImage.thumbnail.isTrue()))
                                    .leftJoin(reservation)
                                    .on(reservation.accommodation.eq(accommodation))
                                    .leftJoin(review).on(review.reservation.eq(reservation))
                                    .where(
                                            reservation.isNotNull(),
                                            reservation.member.id.eq(memberId),
                                            reservation.endDate.before(LocalDateTime.now()))
                                    .orderBy(reservation.id.desc())
                ,
                countQuery -> countQuery.select(reservation.count())
                                        .from(reservation)
                                        .where(
                                                reservation.member.id.eq(memberId),
                                                reservation.endDate.before(LocalDateTime.now())
                                        )
        );
    }
}
