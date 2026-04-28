package project.member.adapter.out.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.member.adapter.out.persistence.model.ChatMemberSearchRow;
import project.member.adapter.out.persistence.model.DefaultProfileRow;
import project.member.adapter.out.persistence.model.TripHistoryRow;
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

    public Optional<DefaultProfileRow> getDefaultProfile(Long memberId) {
        return Optional.ofNullable(
                select(constructor(
                        DefaultProfileRow.class,
                        member.name,
                        member.detail.profileUrl,
                        member.createdAt,
                        member.detail.aboutMe,
                        member.isEmailVerified))
                        .from(member)
                        .where(member.id.eq(memberId))
                        .fetchOne()
        );
    }

    public List<ChatMemberSearchRow> findMembersByName(String name) {
        return select(constructor(
                ChatMemberSearchRow.class,
                member.id,
                member.name,
                member.createdAt,
                member.detail.profileUrl))
                .from(member)
                .where(memberNameContains(name))
                .fetch();
    }

    // TODO : Mybatis 마이그레이션 가능
    private BooleanExpression memberNameContains(String name) {
        return Expressions.stringTemplate("cast({0} as string)", member.name)
                          .contains(name);
    }

    public Page<TripHistoryRow> getTripsHistory(Long memberId, Pageable pageable) {
        return applyPagination(pageable,
                contentQuery ->
                        contentQuery.select(constructor(
                                            TripHistoryRow.class,
                                            reservation.id,
                                            accommodation.id,
                                            accommodationImage.imageUrl,
                                            accommodation.title,
                                            reservation.stayPeriod.startDate,
                                            reservation.stayPeriod.endDate,
                                            review.isNotNull()))
                                    .from(accommodation)
                                    .join(accommodationImage)
                                    .on(accommodationImage.accommodation.eq(accommodation)
                                                                        .and(accommodationImage.thumbnail.isTrue()))
                                    .leftJoin(reservation)
                                    .on(reservation.accommodationId.eq(accommodation.id))
                                    .leftJoin(review).on(review.reservation.eq(reservation))
                                    .where(
                                            reservation.isNotNull(),
                                            reservation.memberId.eq(memberId),
                                            reservation.stayPeriod.endDate.before(LocalDateTime.now()))
                                    .orderBy(reservation.id.desc())
                ,
                countQuery -> countQuery.select(reservation.count())
                                        .from(reservation)
                                        .where(
                                                reservation.memberId.eq(memberId),
                                                reservation.stayPeriod.endDate.before(LocalDateTime.now())
                                        )
        );
    }
}
