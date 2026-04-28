package project.accommodation.application.out.query.model;

public record WishlistInfoView(
        Long accommodationId,
        boolean isInWishlist,
        Long wishlistId,
        String wishlistName
) {
}
