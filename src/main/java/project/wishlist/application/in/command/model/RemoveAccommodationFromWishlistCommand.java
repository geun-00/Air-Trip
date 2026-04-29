package project.wishlist.application.in.command.model;

public record RemoveAccommodationFromWishlistCommand(
        Long wishlistId,
        Long accommodationId,
        Long memberId
) {
}
