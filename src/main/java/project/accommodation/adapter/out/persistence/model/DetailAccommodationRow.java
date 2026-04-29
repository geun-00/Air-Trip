package project.accommodation.adapter.out.persistence.model;

import project.accommodation.domain.Capacity;
import project.wishlist.domain.WishlistName;

public record DetailAccommodationRow(
        Long accommodationId,
        String title,
        Capacity capacity,
        String address,
        double mapX,
        double mapY,
        String checkIn,
        String checkOut,
        String description,
        String number,
        String refundRegulation,
        int price,
        boolean isInWishlist,
        Long wishlistId,
        WishlistName wishlistName,
        Double avgRate
) {

}
