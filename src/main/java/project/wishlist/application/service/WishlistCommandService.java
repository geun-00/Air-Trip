package project.wishlist.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.out.query.ReadAccommodationPort;
import project.accommodation.domain.exception.AccommodationExceptions;
import project.wishlist.application.in.command.AddAccommodationToWishlistUseCase;
import project.wishlist.application.in.command.CreateWishlistUseCase;
import project.wishlist.application.in.command.RemoveAccommodationFromWishlistUseCase;
import project.wishlist.application.in.command.RemoveWishlistUseCase;
import project.wishlist.application.in.command.UpdateWishlistMemoUseCase;
import project.wishlist.application.in.command.UpdateWishlistNameUseCase;
import project.wishlist.application.in.command.model.AddAccommodationToWishlistCommand;
import project.wishlist.application.in.command.model.CreateWishlistCommand;
import project.wishlist.application.in.command.model.CreateWishlistResult;
import project.wishlist.application.in.command.model.RemoveAccommodationFromWishlistCommand;
import project.wishlist.application.in.command.model.RemoveWishlistCommand;
import project.wishlist.application.in.command.model.UpdateWishlistMemoCommand;
import project.wishlist.application.in.command.model.UpdateWishlistNameCommand;
import project.wishlist.application.out.command.DeleteWishlistPort;
import project.wishlist.application.out.command.LoadWishlistPort;
import project.wishlist.application.out.command.SaveWishlistPort;
import project.wishlist.domain.Wishlist;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistCommandService implements CreateWishlistUseCase,
                                               RemoveWishlistUseCase,
                                               UpdateWishlistMemoUseCase,
                                               UpdateWishlistNameUseCase,
                                               AddAccommodationToWishlistUseCase,
                                               RemoveAccommodationFromWishlistUseCase {
    private final LoadWishlistPort loadWishlistPort;
    private final SaveWishlistPort saveWishlistPort;
    private final DeleteWishlistPort deleteWishlistPort;
    private final ReadAccommodationPort readAccommodationPort;

    @Override
    public CreateWishlistResult createWishlist(CreateWishlistCommand command) {
        // TODO : 다른 도메인도 save 시 만들어진 도메인 엔티티 전달하는 방법 통일
        Wishlist savedWishlist = saveWishlistPort.save(Wishlist.create(command.memberId(), command.wishlistName()));

        return new CreateWishlistResult(savedWishlist.getId(), savedWishlist.getName());
    }

    @Override
    public void addAccommodationToWishlist(AddAccommodationToWishlistCommand command) {
        validateAccommodationExists(command.accommodationId());
        Wishlist wishlist = loadWishlistPort.loadOwnerWishlist(command.wishlistId(), command.memberId());

        wishlist.addAccommodation(command.accommodationId());
    }

    @Override
    public void removeAccommodationFromWishlist(RemoveAccommodationFromWishlistCommand command) {
        validateAccommodationExists(command.accommodationId());
        Wishlist wishlist = loadWishlistPort.loadOwnerWishlist(command.wishlistId(), command.memberId());

        wishlist.removeAccommodation(command.accommodationId());
    }

    private void validateAccommodationExists(Long accommodationId) {
        if (!readAccommodationPort.existsById(accommodationId)) {
            throw AccommodationExceptions.notFoundById(accommodationId);
        }
    }

    @Override
    public void updateWishlistName(UpdateWishlistNameCommand command) {
        Wishlist wishlist = loadWishlistPort.loadOwnerWishlist(command.wishlistId(), command.memberId());
        wishlist.updateName(command.wishlistName());
    }

    @Override
    public void removeWishlist(RemoveWishlistCommand command) {
        Wishlist wishlist = loadWishlistPort.loadOwnerWishlist(command.wishlistId(), command.memberId());
        deleteWishlistPort.delete(wishlist);
    }

    @Override
    public void updateMemo(UpdateWishlistMemoCommand command) {
        Wishlist wishlist = loadWishlistPort.loadOwnerWishlist(command.wishlistId(), command.memberId());
        wishlist.updateMemo(command.accommodationId(), command.memo());
    }
}
