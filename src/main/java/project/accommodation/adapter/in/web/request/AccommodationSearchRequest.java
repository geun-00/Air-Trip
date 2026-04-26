package project.accommodation.adapter.in.web.request;

import java.util.List;

public record AccommodationSearchRequest(
        String areaCode,
        List<String> amenities,
        Integer priceGoe,
        Integer priceLoe) {
}
