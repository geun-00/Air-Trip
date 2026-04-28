package project.accommodation.adapter.out.persistence.model;

public record GuestMainAccommodationRow(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        String thumbnailUrl,
        int reservationCount,
        String areaName,
        String areaCode
) {
}
