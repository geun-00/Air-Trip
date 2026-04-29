package project.wishlist.application.in.query.model;

public record WishlistView(
        Long wishlistId,
        String name,
        String thumbnailUrl,
        int savedAccommodations
) {
}
