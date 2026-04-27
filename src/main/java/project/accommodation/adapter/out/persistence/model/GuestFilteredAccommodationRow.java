package project.accommodation.adapter.out.persistence.model;

public record GuestFilteredAccommodationRow(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        int reviewCount
) {
}
