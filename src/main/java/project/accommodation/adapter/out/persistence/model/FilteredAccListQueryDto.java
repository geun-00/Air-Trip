package project.accommodation.adapter.out.persistence.model;

public record FilteredAccListQueryDto(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        int reviewCount,
        boolean isInWishlist,
        Long wishlistId,
        String wishlistName) {

    public FilteredAccListQueryDto(Long accommodationId, String title, int price, double avgRate, int reviewCount) {
        this(accommodationId, title, price, avgRate, reviewCount, false, null, null);
    }
}
