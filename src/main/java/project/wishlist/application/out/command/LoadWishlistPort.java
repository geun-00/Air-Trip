package project.wishlist.application.out.command;

import project.wishlist.domain.Wishlist;

public interface LoadWishlistPort {

    Wishlist loadOwnerWishlist(Long wishlistId, Long memberId);
}
