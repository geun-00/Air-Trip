package project.accommodation.adapter.in.web.response;

import java.time.LocalDateTime;

public record ViewHistoryAccommodationResponse(
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
