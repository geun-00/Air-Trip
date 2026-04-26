package project.accommodation.application.out.query;

import project.accommodation.application.out.query.model.ReservedDateView;

import java.util.List;

public interface LoadReservedDatesPort {

    List<ReservedDateView> loadReservedDates(Long accommodationId);
}
