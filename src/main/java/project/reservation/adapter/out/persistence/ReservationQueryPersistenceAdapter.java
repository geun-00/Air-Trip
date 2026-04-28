package project.reservation.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.accommodation.application.out.query.LoadReservedDatesPort;
import project.accommodation.application.out.query.model.ReservedDateRangeView;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationQueryPersistenceAdapter implements LoadReservedDatesPort {

    private final ReservationQueryRepository reservationQueryRepository;

    @Override
    public List<ReservedDateRangeView> loadReservedDates(Long accommodationId) {
        return reservationQueryRepository.findReservedDatesByAccommodationId(accommodationId)
                                         .stream()
                                         .map(row -> new ReservedDateRangeView(
                                                 row.startDate().toLocalDate(),
                                                 row.endDate().toLocalDate()
                                         ))
                                         .toList();
    }
}
