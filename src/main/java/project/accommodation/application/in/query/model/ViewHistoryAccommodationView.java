package project.accommodation.application.in.query.model;

import java.time.LocalDateTime;

public record ViewHistoryAccommodationView(
        LocalDateTime viewDate,
        Long accommodationId,
        String title,
        double avgRate,
        String thumbnailUrl,
        boolean isInWishlist,
        Long wishlistId,
        String wishlistName
) {
}
