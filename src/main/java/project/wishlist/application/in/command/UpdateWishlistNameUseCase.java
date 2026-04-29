package project.wishlist.application.in.command;

import project.wishlist.application.in.command.model.UpdateWishlistNameCommand;

public interface UpdateWishlistNameUseCase {

    void updateWishlistName(UpdateWishlistNameCommand command);
}
