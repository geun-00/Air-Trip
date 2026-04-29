package project.wishlist.application.in.query;

import project.wishlist.application.in.query.model.WishlistDetailView;

import java.util.List;

public interface GetWishlistDetailsQueryUseCase {

    List<WishlistDetailView> getWishlistDetails(Long wishlistId, Long memberId);
}
