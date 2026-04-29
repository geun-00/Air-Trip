package project.wishlist.application.out.query;

import project.wishlist.application.in.query.model.WishlistSummaryView;

import java.util.List;

public interface LoadWishlistsPort {

    List<WishlistSummaryView> loadWishlists(Long memberId);
}
