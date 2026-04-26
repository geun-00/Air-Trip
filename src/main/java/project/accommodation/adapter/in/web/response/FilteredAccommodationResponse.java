package project.accommodation.adapter.in.web.response;

import java.util.List;

public record FilteredAccommodationResponse(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        int reviewCount,
        List<String> imageUrls,
        boolean isInWishlist,
        Long wishlistId,
        String wishlistName) {
}
