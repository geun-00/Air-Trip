package project.accommodation.application.out.query;

import project.accommodation.application.in.query.model.AccommodationCommonInfoView;

public interface LoadAccommodationCommonInfoPort {

    AccommodationCommonInfoView loadAccommodationCommonInfo(Long accommodationId);
}
