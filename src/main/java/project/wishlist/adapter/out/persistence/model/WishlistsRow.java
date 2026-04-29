package project.wishlist.adapter.out.persistence.model;

import project.wishlist.domain.WishlistName;

public record WishlistsRow(
        Long wishlistId,
        WishlistName name,
        Long recentAccommodationId,
        int savedAccommodations
) {
}
