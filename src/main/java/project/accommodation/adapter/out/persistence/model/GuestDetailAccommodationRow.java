package project.accommodation.adapter.out.persistence.model;

import project.accommodation.domain.Capacity;

public record GuestDetailAccommodationRow(
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
}
