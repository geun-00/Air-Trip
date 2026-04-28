package project.accommodation.application.in.query.model;

import java.time.LocalDate;

public record AccommodationPriceView(
        Long accommodationId,
        LocalDate date,
        int price
) {
}
