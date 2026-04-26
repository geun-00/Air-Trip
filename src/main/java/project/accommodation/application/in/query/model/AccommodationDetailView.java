package project.accommodation.application.in.query.model;

import java.time.LocalDate;
import java.util.List;

public record AccommodationDetailView(
        Long accommodationId,
        String title,
        int maxPeople,
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
        double avgRate,
        DetailImageView images,
        List<String> amenities,
        List<DetailReviewView> reviews,
        List<ReservedDateView> reservedDates
) {

    public record ReservedDateView(LocalDate start, LocalDate end) {
    }
}
