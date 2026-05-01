package project.accommodation.application.out.query;

import org.springframework.data.domain.Page;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.in.query.model.MainAccommodationView;
import project.accommodation.application.out.query.model.MainAccommodationsCondition;
import project.accommodation.application.out.query.model.SearchAccommodationsCondition;
import project.common.domain.StayDatePolicy;

import java.util.List;

public interface ReadAccommodationsPort {

    List<MainAccommodationView> getAreaAccommodations(MainAccommodationsCondition condition);

    Page<FilteredAccommodationView> getFilteredPagingAccommodations(SearchAccommodationsCondition condition);

    int getAccommodationPrice(Long accommodationId, StayDatePolicy stayDatePolicy);
}
