package project.accommodation.adapter.out.persistence.model;

import project.wishlist.domain.WishlistName;

public record WishlistRow(
        Long accommodationId,
        Long wishlistId,
        WishlistName wishlistName
) {
}
