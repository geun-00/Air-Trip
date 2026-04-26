package project.accommodation.application.in.query;

import project.accommodation.adapter.in.web.response.AccommodationPriceResDto;

import java.time.LocalDate;

public interface GetAccommodationPriceQueryUseCase {

    AccommodationPriceResDto getAccommodationPrice(Long accId, LocalDate date);
}
