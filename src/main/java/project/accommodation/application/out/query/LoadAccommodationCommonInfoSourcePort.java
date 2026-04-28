package project.accommodation.application.out.query;

import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.common.domain.StayDatePolicy;

import java.util.List;
import java.util.Map;

public interface LoadAccommodationCommonInfoSourcePort {

    AccommodationCommonInfoView loadAccommodationCommonInfo(Long accommodationId, StayDatePolicy stayDatePolicy);

    Map<Long, AccommodationCommonInfoView> loadAccommodationCommonInfos(List<Long> accommodationIds, StayDatePolicy stayDatePolicy);
}
