package project.accommodation.application.out.query;

import project.accommodation.application.in.query.model.AccommodationCommonInfoView;

import java.util.List;
import java.util.Map;

public interface LoadAccommodationCommonInfoPort {

    AccommodationCommonInfoView loadAccommodationCommonInfo(Long accommodationId);

    Map<Long, AccommodationCommonInfoView> loadAccommodationCommonInfos(List<Long> accommodationIds);
}
