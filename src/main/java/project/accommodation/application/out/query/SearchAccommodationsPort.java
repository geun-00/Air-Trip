package project.accommodation.application.out.query;

import org.springframework.data.domain.Page;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.out.query.model.SearchAccommodationsCondition;

public interface SearchAccommodationsPort {

    Page<FilteredAccommodationView> getFilteredPagingAccommodations(SearchAccommodationsCondition condition);
}
