package project.accommodation.application.in.query;

import project.accommodation.application.in.query.model.MainAccommodationView;

import java.util.List;

public interface GetMainAccommodationsQueryUseCase {

    List<MainAccommodationView> getAccommodations(Long memberId);
}
