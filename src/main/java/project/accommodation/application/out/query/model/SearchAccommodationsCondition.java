package project.accommodation.application.out.query.model;

import org.springframework.data.domain.Pageable;
import project.common.domain.StayDatePolicy;

import java.util.List;

public record SearchAccommodationsCondition(
        String areaCode,
        List<String> amenities,
        Integer priceGoe,
        Integer priceLoe,
        Pageable pageable,
        Long memberId,
        StayDatePolicy stayDatePolicy
) {
}
