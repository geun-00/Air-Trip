package project.accommodation.adapter.out.persistence.model;

import project.wishlist.domain.WishlistName;

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
        WishlistName wishlistName
) {

}
