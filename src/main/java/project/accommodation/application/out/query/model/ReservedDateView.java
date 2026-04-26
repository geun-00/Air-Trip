package project.accommodation.application.out.query.model;

import java.time.LocalDate;

public record ReservedDateView(
        LocalDate startDate,
        LocalDate endDate
) {
}
