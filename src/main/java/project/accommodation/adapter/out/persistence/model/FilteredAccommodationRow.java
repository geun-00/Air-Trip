package project.accommodation.adapter.out.persistence.model;

import java.util.List;

public record FilteredAccommodationRow(
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
