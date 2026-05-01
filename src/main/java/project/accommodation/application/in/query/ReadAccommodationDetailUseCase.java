package project.accommodation.application.in.query;

import project.accommodation.application.in.query.model.AccommodationDetailView;

public interface ReadAccommodationDetailUseCase {

    AccommodationDetailView getDetailAccommodation(Long accommodationId, Long memberId);
}
