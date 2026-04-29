package project.wishlist.application.out.query;

import project.wishlist.application.in.query.model.WishlistDetailView;

import java.util.List;

public interface LoadWishlistDetailsPort {

    List<WishlistDetailView> loadWishlistDetails(Long wishlistId, Long memberId);
}
