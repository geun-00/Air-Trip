package project.wishlist.application.in.command;

import project.wishlist.application.in.command.model.RemoveWishlistCommand;

public interface RemoveWishlistUseCase {

    void removeWishlist(RemoveWishlistCommand command);
}
