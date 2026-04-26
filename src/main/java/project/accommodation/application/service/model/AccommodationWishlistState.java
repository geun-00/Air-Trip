package project.accommodation.application.service.model;

import project.accommodation.application.out.query.model.WishlistInfoView;

public record AccommodationWishlistState(
        boolean isInWishlist,
        Long wishlistId,
        String wishlistName
) {
    public static AccommodationWishlistState empty() {
        return new AccommodationWishlistState(false, null, null);
    }

    public static AccommodationWishlistState from(WishlistInfoView wishlistInfo) {
        if (wishlistInfo == null) {
            return empty();
        }

        return new AccommodationWishlistState(
                wishlistInfo.isInWishlist(),
                wishlistInfo.wishlistId(),
                wishlistInfo.wishlistName()
        );
    }
}
