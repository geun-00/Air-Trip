package project.accommodation.adapter.in.web.response;

import java.time.LocalDate;

public record AccommodationPriceResponse(
        Long accommodationId,
        LocalDate date,
        int price) {
}
