package project.accommodation.application.out.query.model;

import java.time.LocalDate;

public record ReservedDateRangeView(
        LocalDate startDate,
        LocalDate endDate
) {
}
