package project.wishlist.application.in.command.model;

public record UpdateWishlistMemoCommand(
        Long wishlistId,
        Long accommodationId,
        Long memberId,
        String memo
) {
}
