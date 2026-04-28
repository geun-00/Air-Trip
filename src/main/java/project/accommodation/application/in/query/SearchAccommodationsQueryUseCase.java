package project.accommodation.application.in.query;

import project.accommodation.application.in.query.model.AccommodationSearchQuery;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.common.adapter.in.web.response.PageResponse;

public interface SearchAccommodationsQueryUseCase {

    PageResponse<FilteredAccommodationView> getFilteredPagingAccommodations(
            AccommodationSearchQuery searchQuery,
            Long memberId
    );
}
