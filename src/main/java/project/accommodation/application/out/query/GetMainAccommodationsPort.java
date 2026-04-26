package project.accommodation.application.out.query;

import project.accommodation.application.in.query.model.MainAccommodationView;
import project.accommodation.application.out.query.model.MainAccommodationsCondition;

import java.util.List;

public interface GetMainAccommodationsPort {

    List<MainAccommodationView> getAreaAccommodations(MainAccommodationsCondition condition);
}
