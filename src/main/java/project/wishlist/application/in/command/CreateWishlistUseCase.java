package project.wishlist.application.in.command;

import project.wishlist.application.in.command.model.CreateWishlistCommand;
import project.wishlist.application.in.command.model.CreateWishlistResult;

public interface CreateWishlistUseCase {

    CreateWishlistResult createWishlist(CreateWishlistCommand command);
}
