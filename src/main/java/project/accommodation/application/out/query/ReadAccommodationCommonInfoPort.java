package project.accommodation.application.out.query;

import project.accommodation.application.in.query.model.AccommodationCommonInfoView;

import java.util.List;
import java.util.Map;

public interface ReadAccommodationCommonInfoPort {

    AccommodationCommonInfoView getById(Long accommodationId);

    Map<Long, AccommodationCommonInfoView> getAllByIds(List<Long> accommodationIds);
}
