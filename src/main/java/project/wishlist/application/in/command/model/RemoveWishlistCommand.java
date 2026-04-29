package project.wishlist.application.in.command.model;

public record RemoveWishlistCommand(
        Long wishlistId,
        Long memberId
) {
}
