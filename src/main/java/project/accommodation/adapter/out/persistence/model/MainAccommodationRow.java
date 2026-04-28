package project.accommodation.adapter.out.persistence.model;

public record MainAccommodationRow(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        String thumbnailUrl,
        boolean isInWishlist,
        Long wishlistId,
        String wishlistName,
        int reservationCount,
        String areaName,
        String areaCode
) {

}
