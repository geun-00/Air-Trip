package project.wishlist.application.in.command;

import project.wishlist.application.in.command.model.UpdateWishlistMemoCommand;

public interface UpdateWishlistMemoUseCase {

    void updateMemo(UpdateWishlistMemoCommand command);
}
