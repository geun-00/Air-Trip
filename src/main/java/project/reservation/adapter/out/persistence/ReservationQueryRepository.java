package project.reservation.adapter.out.persistence;

import org.springframework.stereotype.Repository;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.reservation.adapter.out.persistence.model.ReservedDateRow;
import project.reservation.domain.Reservation;

import java.time.LocalDateTime;
import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static project.reservation.domain.QReservation.reservation;
import static project.reservation.domain.ReservationStatus.CONFIRMED;

@Repository
public class ReservationQueryRepository extends CustomQuerydslRepositorySupport {

    public ReservationQueryRepository() {
        super(Reservation.class);
    }

    public boolean existsConfirmedReservation(Long accId, LocalDateTime from, LocalDateTime to) {
        return getQueryFactory()
                .selectOne()
                .from(reservation)
                .where(
                        reservation.accommodation.id.eq(accId),
                        reservation.status.eq(CONFIRMED),
                        reservation.stayPeriod.startDate.lt(to),
                        reservation.stayPeriod.endDate.gt(from)
                )
                .fetchFirst() != null;
    }

    public List<ReservedDateRow> findReservedDatesByAccommodationId(Long accommodationId) {
        return getQueryFactory()
                .select(constructor(ReservedDateRow.class,
                        reservation.stayPeriod.startDate,
                        reservation.stayPeriod.endDate))
                .from(reservation)
                .where(reservation.accommodation.id.eq(accommodationId)
                                                   .and(reservation.status.eq(CONFIRMED))
                                                   .and(reservation.stayPeriod.endDate.after(LocalDateTime.now())))
                .fetch();
    }
}
