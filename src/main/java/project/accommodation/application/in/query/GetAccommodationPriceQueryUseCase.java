package project.accommodation.application.in.query;

import project.accommodation.application.in.query.model.AccommodationPriceView;

import java.time.LocalDate;

public interface GetAccommodationPriceQueryUseCase {

    AccommodationPriceView getAccommodationPrice(Long accommodationId, LocalDate date);
}
