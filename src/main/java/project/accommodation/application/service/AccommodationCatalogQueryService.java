package project.accommodation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.in.query.GetAccommodationPriceQueryUseCase;
import project.accommodation.application.in.query.GetMainAccommodationsQueryUseCase;
import project.accommodation.application.in.query.SearchAccommodationsQueryUseCase;
import project.accommodation.application.in.query.model.AccommodationPriceView;
import project.accommodation.application.in.query.model.AccommodationSearchQuery;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.in.query.model.MainAccommodationView;
import project.accommodation.application.out.query.GetAccommodationPricePort;
import project.accommodation.application.out.query.GetMainAccommodationsPort;
import project.accommodation.application.out.query.SearchAccommodationsPort;
import project.accommodation.application.out.query.model.MainAccommodationsCondition;
import project.accommodation.application.out.query.model.SearchAccommodationsCondition;
import project.common.adapter.in.web.response.PageResponse;
import project.common.domain.StayDatePolicy;
import project.infrastructure.time.DateManager;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationCatalogQueryService implements GetMainAccommodationsQueryUseCase,
                                                         SearchAccommodationsQueryUseCase,
                                                         GetAccommodationPriceQueryUseCase {

    private final DateManager dateManager;
    private final SearchAccommodationsPort searchAccommodationsPort;
    private final GetMainAccommodationsPort getMainAccommodationsPort;
    private final GetAccommodationPricePort getAccommodationPricePort;

    @Override
    public List<MainAccommodationView> getAccommodations(Long memberId) {
        StayDatePolicy stayDatePolicy = dateManager.getStayDatePolicy(LocalDate.now());
        MainAccommodationsCondition condition = new MainAccommodationsCondition(stayDatePolicy, memberId);

        return getMainAccommodationsPort.getAreaAccommodations(condition);
    }

    @Override
    public PageResponse<FilteredAccommodationView> getFilteredPagingAccommodations(
            AccommodationSearchQuery searchQuery,
            Long memberId
    ) {
        StayDatePolicy stayDatePolicy = dateManager.getStayDatePolicy(LocalDate.now());

        SearchAccommodationsCondition condition = new SearchAccommodationsCondition(
                searchQuery.areaCode(),
                searchQuery.amenities(),
                searchQuery.priceGoe(),
                searchQuery.priceLoe(),
                searchQuery.pageQuery(),
                memberId,
                stayDatePolicy
        );

        return searchAccommodationsPort.getFilteredPagingAccommodations(condition);
    }

    @Override
    public AccommodationPriceView getAccommodationPrice(Long accommodationId, LocalDate date) {
        StayDatePolicy stayDatePolicy = dateManager.getStayDatePolicy(date);
        int price = getAccommodationPricePort.getAccommodationPrice(accommodationId, stayDatePolicy);

        return new AccommodationPriceView(accommodationId, date, price);
    }
}
