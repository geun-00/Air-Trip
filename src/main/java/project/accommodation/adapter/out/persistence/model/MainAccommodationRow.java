package project.accommodation.adapter.out.persistence.model;

public record MainAccommodationRow(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        String thumbnailUrl,
        boolean isInWishlist,
        Long wishlistId,
        String wishlistName,
        int reservationCount,
        String areaName,
        String areaCode
) {

    // TODO : 모델 분리 or @QueryProjection 등 개선하기
    public MainAccommodationRow(
            Long accommodationId,
            String title,
            int price,
            double avgRate,
            String thumbnailUrl,
            int reservationCount,
            String areaName,
            String areaCode
    ) {
        this(accommodationId, title, price, avgRate, thumbnailUrl, false, null, null, reservationCount, areaName, areaCode);
    }
}
