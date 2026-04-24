package project.wishlist.adapter.in.web.response;

public record WishlistsResponse(
        Long wishlistId,
        String name,
        String thumbnailUrl,
        int savedAccommodations) {
}
