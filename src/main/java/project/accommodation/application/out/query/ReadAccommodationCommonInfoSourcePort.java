package project.accommodation.application.out.query;

import project.accommodation.application.in.query.model.AccommodationCommonInfoView;
import project.common.domain.StayDatePolicy;

import java.util.List;
import java.util.Map;

public interface ReadAccommodationCommonInfoSourcePort {

    AccommodationCommonInfoView getByIdAndStayDatePolicy(Long accommodationId, StayDatePolicy stayDatePolicy);

    Map<Long, AccommodationCommonInfoView> getAllByIdsAndStayDatePolicy(List<Long> accommodationIds, StayDatePolicy stayDatePolicy);
}
