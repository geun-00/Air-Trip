package project.wishlist.adapter.out.persistence.model;

public record WishlistInfoRow(
        Long accommodationId,
        boolean isInWishlist,
        Long wishlistId,
        String wishlistName
) {
}
