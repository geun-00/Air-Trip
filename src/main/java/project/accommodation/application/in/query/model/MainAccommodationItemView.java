package project.accommodation.application.in.query.model;

public record MainAccommodationItemView(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        String thumbnailUrl,
        boolean isInWishlist,
        String wishlistName,
        Long wishlistId,
        String areaName,
        String areaCode
) {
}
