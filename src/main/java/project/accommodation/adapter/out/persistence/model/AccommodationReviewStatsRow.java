package project.accommodation.adapter.out.persistence.model;

public record AccommodationReviewStatsRow(
        Long accommodationId,
        double avgRate,
        int reviewCount
) {
}
