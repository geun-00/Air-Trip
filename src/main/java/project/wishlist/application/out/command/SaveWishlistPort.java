package project.wishlist.application.out.command;

import project.wishlist.domain.Wishlist;

public interface SaveWishlistPort {

    Wishlist save(Wishlist wishlist);
}
