package project.member.application.query.model;

import java.time.LocalDate;

public record TripHistoryView(
        Long reservationId,
        Long accommodationId,
        String thumbnailUrl,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        boolean hasReviewed
) {
}
