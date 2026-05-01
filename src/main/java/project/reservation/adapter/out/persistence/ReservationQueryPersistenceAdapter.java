package project.reservation.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.accommodation.application.out.query.ReadReservedDatesPort;
import project.accommodation.application.out.query.model.ReservedDateRangeView;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationQueryPersistenceAdapter implements ReadReservedDatesPort {

    private final ReservationQueryRepository reservationQueryRepository;

    @Override
    public List<ReservedDateRangeView> getByAccommodationId(Long accommodationId) {
        return reservationQueryRepository.findReservedDatesByAccommodationId(accommodationId)
                                         .stream()
                                         .map(row -> new ReservedDateRangeView(
                                                 row.startDate().toLocalDate(),
                                                 row.endDate().minusNanos(1).toLocalDate()
                                         ))
                                         .toList();
    }
}
