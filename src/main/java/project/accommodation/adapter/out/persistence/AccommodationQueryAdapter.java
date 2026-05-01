package project.accommodation.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.in.query.model.MainAccommodationView;
import project.accommodation.application.out.query.ReadAccommodationsPort;
import project.accommodation.application.out.query.model.MainAccommodationsCondition;
import project.accommodation.application.out.query.model.SearchAccommodationsCondition;
import project.accommodation.domain.exception.AccommodationExceptions;
import project.common.domain.StayDatePolicy;

import java.util.List;

import static project.accommodation.adapter.out.persistence.AccommodationQueryViewMapper.toMainViews;

@Repository
@RequiredArgsConstructor
public class AccommodationQueryAdapter implements ReadAccommodationsPort {

    private final AccommodationQueryRepository accommodationQueryRepository;
    private final AccommodationRepository accommodationRepository;

    @Override
    public List<MainAccommodationView> getAreaAccommodations(MainAccommodationsCondition condition) {
        return toMainViews(
                accommodationQueryRepository.getAreaAccommodations(
                        condition.stayDatePolicy(),
                        condition.memberId()
                )
        );
    }

    @Override
    public Page<FilteredAccommodationView> getFilteredPagingAccommodations(SearchAccommodationsCondition condition) {
        return accommodationQueryRepository.getFilteredPagingAccommodations(condition)
                                           .map(AccommodationQueryViewMapper::toFilteredView);
    }

    @Override
    public int getAccommodationPrice(Long accommodationId, StayDatePolicy stayDatePolicy) {
        Integer price = accommodationRepository.findPrice(
                accommodationId,
                stayDatePolicy.season(),
                stayDatePolicy.dayType()
        );
        if (price == null) {
            throw AccommodationExceptions.priceNotFound(accommodationId, stayDatePolicy);
        }
        return price;
    }
}
