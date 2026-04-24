package project.wishlist.adapter.out.persistence.model;

public record WishlistDetailQueryDto(
        Long accommodationId,
        String wishlistName,
        String title,
        String description,
        double mapX,
        double mapY,
        double avgRate,
        String memo) {
}
