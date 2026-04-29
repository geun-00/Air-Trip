package project.wishlist.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.wishlist.domain.Wishlist;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("""
            SELECT DISTINCT w
            FROM Wishlist w
            LEFT JOIN FETCH w.accommodations
            WHERE w.id = :wishlistId
            AND w.memberId = :memberId
            """)
    Optional<Wishlist> findByIdAndMemberIdWithAccommodations(
            @Param("wishlistId") Long wishlistId,
            @Param("memberId") Long memberId
    );
}
