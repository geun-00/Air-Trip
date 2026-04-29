package project.wishlist.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.wishlist.application.out.command.DeleteWishlistPort;
import project.wishlist.application.out.command.LoadWishlistPort;
import project.wishlist.application.out.command.SaveWishlistPort;
import project.wishlist.domain.Wishlist;
import project.wishlist.domain.exception.WishlistExceptions;

@Repository
@RequiredArgsConstructor
public class WishlistCommandPersistenceAdapter implements LoadWishlistPort,
                                                          SaveWishlistPort,
                                                          DeleteWishlistPort {
    private final WishlistRepository wishlistRepository;

    @Override
    public Wishlist loadOwnerWishlist(Long wishlistId, Long memberId) {
        return wishlistRepository.findByIdAndMemberIdWithAccommodations(wishlistId, memberId)
                                 .orElseThrow(() -> WishlistExceptions.notFoundByIdAndMemberId(wishlistId, memberId));
    }

    @Override
    public Wishlist save(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    @Override
    public void delete(Wishlist wishlist) {
        wishlistRepository.delete(wishlist);
    }
}
