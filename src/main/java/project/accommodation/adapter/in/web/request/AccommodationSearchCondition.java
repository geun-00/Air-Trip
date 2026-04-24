package project.accommodation.adapter.in.web.request;

import java.util.List;

public record AccommodationSearchCondition(
        String areaCode,
        List<String> amenities,
        Integer priceGoe,
        Integer priceLoe) {
}
