package project.accommodation.application.in.query;

import project.accommodation.application.in.query.model.AccommodationDetailView;

public interface GetAccommodationDetailQueryUseCase {

    AccommodationDetailView getDetailAccommodation(Long accommodationId, Long memberId);
}
