package project.member.adapter.in.web.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TripHistoryResponse(
        Long reservationId,
        Long accommodationId,
        String thumbnailUrl,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        boolean hasReviewed) {

    public TripHistoryResponse(Long reservationId, Long accommodationId, String thumbnailUrl, String title, LocalDateTime startDate, LocalDateTime endDate, boolean hasReviewed) {
        this(reservationId, accommodationId, thumbnailUrl, title, startDate.toLocalDate(), endDate.toLocalDate(), hasReviewed);
    }
}
