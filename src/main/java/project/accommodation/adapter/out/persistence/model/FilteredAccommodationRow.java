package project.accommodation.adapter.out.persistence.model;

import java.util.List;

public record FilteredAccommodationRow(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        int reviewCount,
        List<String> imageUrls,
        boolean isInWishlist,
        Long wishlistId,
        String wishlistName
) {

    // TODO : 제거 및 개선
    public FilteredAccommodationRow(Long accommodationId, String title, int price, double avgRate, int reviewCount) {
        this(accommodationId, title, price, avgRate, reviewCount, List.of(), false, null, null);
    }

    // TODO : 제거 및 개선
    public FilteredAccommodationRow(
            Long accommodationId,
            String title,
            int price,
            double avgRate,
            int reviewCount,
            boolean isInWishlist,
            Long wishlistId,
            String wishlistName
    ) {
        this(accommodationId, title, price, avgRate, reviewCount, List.of(), isInWishlist, wishlistId, wishlistName);
    }
}
