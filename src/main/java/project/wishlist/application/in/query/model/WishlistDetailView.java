package project.wishlist.application.in.query.model;

import java.util.List;

public record WishlistDetailView(
        Long accommodationId,
        String wishlistName,
        String title,
        String description,
        double mapX,
        double mapY,
        double avgRate,
        List<String> imageUrls,
        String memo
) {
}
