package project.accommodation.adapter.in.web.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DetailAccommodationResponse(
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
        DetailImageResponse images,
        List<String> amenities,
        List<DetailReviewResponse> reviews,
        List<ReservedDateResponse> reservedDates
) {

    public record DetailReviewResponse(
            Long memberId,
            String memberName,
            String profileUrl,
            LocalDateTime memberCreatedDate,
            LocalDateTime reviewCreatedDate,
            double rating,
            String content
    ) {
    }

    public record DetailImageResponse(String thumbnail, List<String> others) {
    }

    public record ReservedDateResponse(LocalDate start, LocalDate end) {
    }
}
