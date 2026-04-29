package project.accommodation.adapter.out.persistence.model;

import project.wishlist.domain.WishlistName;

public record MainAccommodationRow(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        String thumbnailUrl,
        boolean isInWishlist,
        Long wishlistId,
        WishlistName wishlistName,
        int reservationCount,
        String areaName,
        String areaCode
) {

}
