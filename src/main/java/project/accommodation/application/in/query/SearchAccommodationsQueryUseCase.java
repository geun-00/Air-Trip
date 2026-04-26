package project.accommodation.application.in.query;

import org.springframework.data.domain.Pageable;
import project.accommodation.adapter.in.web.request.AccommodationSearchCondition;
import project.accommodation.adapter.in.web.response.FilteredAccListResDto;
import project.common.adapter.in.web.response.PageResponse;

public interface SearchAccommodationsQueryUseCase {

    PageResponse<FilteredAccListResDto> getFilteredPagingAccommodations(
            AccommodationSearchCondition searchDto,
            Long memberId,
            Pageable pageable
    );
}
