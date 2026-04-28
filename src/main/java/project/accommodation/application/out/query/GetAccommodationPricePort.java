package project.accommodation.application.out.query;

import project.common.domain.StayDatePolicy;

public interface GetAccommodationPricePort {

    int getAccommodationPrice(Long accommodationId, StayDatePolicy stayDatePolicy);
}
