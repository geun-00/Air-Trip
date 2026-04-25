package project.member.adapter.in.web.response;

import java.time.LocalDate;

public record TripHistoryResponse(
        Long reservationId,
        Long accommodationId,
        String thumbnailUrl,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        boolean hasReviewed) {
}
