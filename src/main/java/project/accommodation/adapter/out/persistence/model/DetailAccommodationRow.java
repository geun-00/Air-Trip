package project.accommodation.adapter.out.persistence.model;

import project.accommodation.domain.Capacity;

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
        String wishlistName,
        Double avgRate
) {

    public DetailAccommodationRow(
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
            Double avgRate
    ) {
        this(accommodationId, title, capacity, address, mapX, mapY, checkIn, checkOut, description, number, refundRegulation, price, false, null, null, avgRate);
    }
}
