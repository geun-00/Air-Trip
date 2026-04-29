package project.wishlist.application.in.command;

import project.wishlist.application.in.command.model.RemoveAccommodationFromWishlistCommand;

public interface RemoveAccommodationFromWishlistUseCase {

    void removeAccommodationFromWishlist(RemoveAccommodationFromWishlistCommand command);
}
