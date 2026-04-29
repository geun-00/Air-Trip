package project.wishlist.application.in.command.model;

public record AddAccommodationToWishlistCommand(
        Long wishlistId,
        Long accommodationId,
        Long memberId
) {
}
