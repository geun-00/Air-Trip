package project.wishlist.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import project.wishlist.domain.Wishlist;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByIdAndMemberId(Long wishlistId, Long memberId);
}