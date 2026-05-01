package project.accommodation.application.in.query;

import org.springframework.data.domain.Page;
import project.accommodation.application.in.query.model.AccommodationPriceView;
import project.accommodation.application.in.query.model.AccommodationSearchQuery;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.in.query.model.MainAccommodationView;

import java.time.LocalDate;
import java.util.List;

public interface ReadAccommodationsUseCase {

    List<MainAccommodationView> getAccommodations(Long memberId);

    Page<FilteredAccommodationView> getFilteredPagingAccommodations(
            AccommodationSearchQuery searchQuery,
            Long memberId
    );

    AccommodationPriceView getAccommodationPrice(Long accommodationId, LocalDate date);
}
