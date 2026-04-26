package project.accommodation.application.out.query;

import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.common.adapter.in.web.response.PageResponse;

public interface SearchAccommodationsPort {

    PageResponse<FilteredAccommodationView> getFilteredPagingAccommodations(SearchAccommodationsCondition condition);
}
