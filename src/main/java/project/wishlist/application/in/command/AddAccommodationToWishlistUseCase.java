package project.wishlist.application.in.command;

import project.wishlist.application.in.command.model.AddAccommodationToWishlistCommand;

public interface AddAccommodationToWishlistUseCase {

    void addAccommodationToWishlist(AddAccommodationToWishlistCommand command);
}
