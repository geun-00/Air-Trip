package project.wishlist.application.in.command.model;

public record CreateWishlistResult(
        Long wishlistId,
        String wishlistName
) {
}
