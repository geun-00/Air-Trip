package project.accommodation.adapter.out.persistence.model;

import project.accommodation.domain.ReviewCount;
import project.common.domain.Rating;
import project.wishlist.domain.WishlistName;

import java.util.List;

public record FilteredAccommodationRow(
        Long accommodationId,
        String title,
        int price,
        Rating avgRate,
        ReviewCount reviewCount,
        List<String> imageUrls,
        boolean isInWishlist,
        Long wishlistId,
        WishlistName wishlistName
) {

}
