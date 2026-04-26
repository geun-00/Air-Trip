package project.accommodation.application.in.query.model;

import project.common.application.query.PageQuery;

import java.util.List;

public record AccommodationSearchQuery(
        String areaCode,
        List<String> amenities,
        Integer priceGoe,
        Integer priceLoe,
        PageQuery pageQuery
) {
}
