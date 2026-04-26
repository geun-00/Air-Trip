package project.accommodation.application.out.query;

import project.accommodation.application.in.query.model.MainAccommodationView;

import java.util.List;

public interface GetMainAccommodationsPort {

    List<MainAccommodationView> getAreaAccommodations(MainAccommodationsCondition condition);
}
