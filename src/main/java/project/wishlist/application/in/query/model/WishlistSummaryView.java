package project.wishlist.application.in.query.model;

public record WishlistSummaryView(
        Long wishlistId,
        String name,
        Long recentAccommodationId,
        int savedAccommodations
) {
}
