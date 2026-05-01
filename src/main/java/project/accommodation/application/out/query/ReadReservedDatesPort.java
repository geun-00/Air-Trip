package project.accommodation.application.out.query;

import project.accommodation.application.out.query.model.ReservedDateRangeView;

import java.util.List;

public interface ReadReservedDatesPort {

    List<ReservedDateRangeView> getByAccommodationId(Long accommodationId);
}
