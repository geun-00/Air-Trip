package project.accommodation.application.in.query.model;

import org.springframework.data.domain.Pageable;

import java.util.List;

public record AccommodationSearchQuery(
        String areaCode,
        List<String> amenities,
        Integer priceGoe,
        Integer priceLoe,
        Pageable pageable
) {
}
