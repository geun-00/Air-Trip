package project.accommodation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.in.query.ReadAccommodationsUseCase;
import project.accommodation.application.in.query.model.AccommodationPriceView;
import project.accommodation.application.in.query.model.AccommodationSearchQuery;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.in.query.model.MainAccommodationView;
import project.accommodation.application.out.query.ReadAccommodationsPort;
import project.accommodation.application.out.query.model.MainAccommodationsCondition;
import project.accommodation.application.out.query.model.SearchAccommodationsCondition;
import project.common.domain.StayDatePolicy;
import project.infrastructure.time.StayDatePolicyProvider;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationQueryService implements ReadAccommodationsUseCase {

    private final StayDatePolicyProvider stayDatePolicyProvider;
    private final ReadAccommodationsPort readAccommodationsPort;

    @Override
    public List<MainAccommodationView> getAccommodations(Long memberId) {
        StayDatePolicy stayDatePolicy = stayDatePolicyProvider.todayStayDatePolicy();
        MainAccommodationsCondition condition = new MainAccommodationsCondition(stayDatePolicy, memberId);

        return readAccommodationsPort.getAreaAccommodations(condition);
    }

    @Override
    public Page<FilteredAccommodationView> getFilteredPagingAccommodations(
            AccommodationSearchQuery searchQuery,
            Long memberId
    ) {
        StayDatePolicy stayDatePolicy = stayDatePolicyProvider.todayStayDatePolicy();

        SearchAccommodationsCondition condition = new SearchAccommodationsCondition(
                searchQuery.areaCode(),
                searchQuery.amenities(),
                searchQuery.priceGoe(),
                searchQuery.priceLoe(),
                searchQuery.pageable(),
                memberId,
                stayDatePolicy
        );

        return readAccommodationsPort.getFilteredPagingAccommodations(condition);
    }

    @Override
    public AccommodationPriceView getAccommodationPrice(Long accommodationId, LocalDate date) {
        StayDatePolicy stayDatePolicy = stayDatePolicyProvider.getStayDatePolicy(date);
        int price = readAccommodationsPort.getAccommodationPrice(accommodationId, stayDatePolicy);

        return new AccommodationPriceView(accommodationId, date, price);
    }
}
