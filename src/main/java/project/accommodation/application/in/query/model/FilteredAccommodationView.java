package project.accommodation.application.in.query.model;

import java.util.List;

public record FilteredAccommodationView(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        int reviewCount,
        List<String> imageUrls,
        boolean isInWishlist,
        Long wishlistId,
        String wishlistName
) {
}
