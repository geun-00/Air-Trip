package project.accommodation.application.out.query;

import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.common.domain.StayDatePolicy;

public interface LoadAccommodationCommonInfoSourcePort {

    AccommodationCommonInfoView loadAccommodationCommonInfo(Long accommodationId, StayDatePolicy stayDatePolicy);
}
