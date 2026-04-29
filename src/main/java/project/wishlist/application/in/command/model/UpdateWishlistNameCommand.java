package project.wishlist.application.in.command.model;

public record UpdateWishlistNameCommand(
        Long wishlistId,
        Long memberId,
        String wishlistName
) {
}
