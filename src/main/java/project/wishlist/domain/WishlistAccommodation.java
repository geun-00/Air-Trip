package project.wishlist.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wishlist_accommodations", uniqueConstraints = @UniqueConstraint(name = "uk_wishlist_accommodations_wishlist_accommodation", columnNames = {"wishlist_id", "accommodation_id"}))
class WishlistAccommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_accommodation_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @Column(name = "accommodation_id", nullable = false)
    private Long accommodationId;

    @Column(name = "memo", length = 250)
    private WishlistMemo memo;

    static WishlistAccommodation create(Wishlist wishlist, Long accommodationId) {
        return new WishlistAccommodation(wishlist, accommodationId);
    }

    private WishlistAccommodation(Wishlist wishlist, Long accommodationId) {
        this.wishlist = wishlist;
        this.accommodationId = accommodationId;
    }

    void updateMemo(String newMemo) {
        this.memo = WishlistMemo.from(newMemo);
    }

    boolean isAccommodation(Long accommodationId) {
        return this.accommodationId.equals(accommodationId);
    }
}
