package project.wishlist.application.out.command;

import project.wishlist.domain.Wishlist;

public interface DeleteWishlistPort {

    void delete(Wishlist wishlist);
}
