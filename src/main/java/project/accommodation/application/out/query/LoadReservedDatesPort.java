package project.accommodation.application.out.query;

import project.accommodation.application.out.query.model.ReservedDateRangeView;

import java.util.List;

public interface LoadReservedDatesPort {

    List<ReservedDateRangeView> loadReservedDates(Long accommodationId);
}
