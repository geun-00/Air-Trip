package project.accommodation.application.out.query.model;

import project.common.domain.StayDatePolicy;

public record MainAccommodationsCondition(
        StayDatePolicy stayDatePolicy,
        Long memberId
) {
}
