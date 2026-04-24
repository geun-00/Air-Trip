package project.reservation.adapter.out.persistence;

import com.querydsl.core.types.Projections;
import org.springframework.stereotype.Repository;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.reservation.adapter.out.persistence.model.ReservedDateQueryDto;
import project.reservation.domain.Reservation;

import java.time.LocalDateTime;
import java.util.List;

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
                        reservation.startDate.lt(to),
                        reservation.endDate.gt(from)
                )
                .fetchFirst() != null;
    }

    public List<ReservedDateQueryDto> findReservedDatesByAccommodationId(Long accommodationId) {
        return getQueryFactory()
                .select(Projections.constructor(ReservedDateQueryDto.class,
                        reservation.startDate,
                        reservation.endDate))
                .from(reservation)
                .where(reservation.accommodation.id.eq(accommodationId)
                                                   .and(reservation.status.eq(CONFIRMED))
                                                   .and(reservation.endDate.after(LocalDateTime.now())))
                .fetch();
    }
}
