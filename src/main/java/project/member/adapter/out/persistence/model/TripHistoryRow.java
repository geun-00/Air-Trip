package project.member.adapter.out.persistence.model;

import java.time.LocalDateTime;

public record TripHistoryRow(
        Long reservationId,
        Long accommodationId,
        String thumbnailUrl,
        String title,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean hasReviewed
) {
}
