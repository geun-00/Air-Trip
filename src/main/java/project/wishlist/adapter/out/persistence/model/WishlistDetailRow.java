package project.wishlist.adapter.out.persistence.model;

import project.wishlist.domain.WishlistName;
import project.wishlist.domain.WishlistMemo;

public record WishlistDetailRow(
        Long accommodationId,
        WishlistName wishlistName,
        String title,
        String description,
        double mapX,
        double mapY,
        double avgRate,
        WishlistMemo memo
) {
}
