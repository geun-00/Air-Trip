package project.accommodation.application.out.query;

import project.common.domain.StayDatePolicy;

public record MainAccommodationsCondition(
        StayDatePolicy stayDatePolicy,
        Long memberId
) {
}
