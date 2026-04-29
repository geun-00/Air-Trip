package project.wishlist.application.in.command.model;

public record CreateWishlistCommand(
        Long memberId,
        String wishlistName
) {
}
