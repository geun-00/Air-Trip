package project.accommodation.adapter.in.web.response;

/**
 * 메인 화면 지역별 각 숙소 최소 정보
 */
public record MainAccommodationsResponse(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        String thumbnailUrl,
        boolean isInWishlist,
        String wishlistName,
        Long wishlistId) {
}
