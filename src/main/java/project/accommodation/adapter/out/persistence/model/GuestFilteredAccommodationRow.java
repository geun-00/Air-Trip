package project.accommodation.adapter.out.persistence.model;

import project.accommodation.domain.ReviewCount;
import project.common.domain.Rating;

public record GuestFilteredAccommodationRow(
        Long accommodationId,
        String title,
        int price,
        Rating avgRate,
        ReviewCount reviewCount
) {
}
