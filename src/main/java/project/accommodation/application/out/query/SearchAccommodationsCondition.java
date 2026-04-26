package project.accommodation.application.out.query;

import project.common.application.query.PageQuery;
import project.common.domain.StayDatePolicy;

import java.util.List;

public record SearchAccommodationsCondition(
        String areaCode,
        List<String> amenities,
        Integer priceGoe,
        Integer priceLoe,
        PageQuery pageQuery,
        Long memberId,
        StayDatePolicy stayDatePolicy
) {
}
