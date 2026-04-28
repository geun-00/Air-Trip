package project.accommodation.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.accommodation.adapter.out.persistence.model.FilteredAccommodationRow;
import project.accommodation.adapter.out.persistence.model.MainAccommodationRow;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.in.query.model.MainAccommodationItemView;
import project.accommodation.application.in.query.model.MainAccommodationView;
import project.accommodation.application.out.query.GetAccommodationPricePort;
import project.accommodation.application.out.query.GetMainAccommodationsPort;
import project.accommodation.application.out.query.model.MainAccommodationsCondition;
import project.accommodation.application.out.query.model.SearchAccommodationsCondition;
import project.accommodation.application.out.query.SearchAccommodationsPort;
import project.accommodation.domain.exception.AccommodationExceptions;
import project.common.adapter.in.web.response.PageResponse;
import project.common.application.query.PageQuery;
import project.common.domain.StayDatePolicy;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class AccommodationQueryPersistenceAdapter implements GetMainAccommodationsPort,
                                                             SearchAccommodationsPort,
                                                             GetAccommodationPricePort {

    private final AccommodationQueryRepository accommodationQueryRepository;
    private final AccommodationRepository accommodationRepository;

    @Override
    public List<MainAccommodationView> getAreaAccommodations(MainAccommodationsCondition condition) {
        return accommodationQueryRepository.getAreaAccommodations(
                                                   condition.stayDatePolicy(),
                                                   condition.memberId()
                                           )
                                           .stream()
                                           .map(this::convertToView)
                                           .collect(
                                                   groupingBy(
                                                           item -> new AreaKey(item.areaName(), item.areaCode()),
                                                           mapping(item -> item, toList())
                                                   )
                                           )
                                           .entrySet()
                                           .stream()
                                           .map(entry -> new MainAccommodationView(
                                                   entry.getKey().areaName(),
                                                   entry.getKey().areaCode(),
                                                   entry.getValue()
                                           ))
                                           .toList();
    }

    private MainAccommodationItemView convertToView(MainAccommodationRow row) {
        return new MainAccommodationItemView(
                row.accommodationId(),
                row.title(),
                row.price(),
                row.avgRate(),
                row.thumbnailUrl(),
                row.isInWishlist(),
                row.wishlistName(),
                row.wishlistId(),
                row.areaName(),
                row.areaCode()
        );
    }

    @Override
    public PageResponse<FilteredAccommodationView> getFilteredPagingAccommodations(SearchAccommodationsCondition condition) {
        PageQuery pageQuery = condition.pageQuery();
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size());

        Page<FilteredAccommodationView> page = accommodationQueryRepository.getFilteredPagingAccommodations(condition, pageable)
                                                                           .map(this::convertToView);
        return PageResponse.from(page);
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

    private FilteredAccommodationView convertToView(FilteredAccommodationRow row) {
        return new FilteredAccommodationView(
                row.accommodationId(),
                row.title(),
                row.price(),
                row.avgRate(),
                row.reviewCount(),
                row.imageUrls(),
                row.isInWishlist(),
                row.wishlistId(),
                row.wishlistName()
        );
    }

    private record AreaKey(String areaName, String areaCode) {
    }
}
