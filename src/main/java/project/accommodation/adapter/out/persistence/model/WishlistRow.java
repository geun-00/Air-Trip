package project.accommodation.adapter.out.persistence.model;

public record WishlistRow(
        Long accommodationId,
        Long wishlistId,
        String wishlistName
) {
}
