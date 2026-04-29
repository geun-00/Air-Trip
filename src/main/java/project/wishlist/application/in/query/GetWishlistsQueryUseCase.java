package project.wishlist.application.in.query;

import project.wishlist.application.in.query.model.WishlistView;

import java.util.List;

public interface GetWishlistsQueryUseCase {

    List<WishlistView> getWishlists(Long memberId);
}
