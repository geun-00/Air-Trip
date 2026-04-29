package project.wishlist.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.adapter.out.persistence.BaseEntity;
import project.wishlist.domain.exception.WishlistExceptions;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wishlists")
public class Wishlist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "name", length = 50, nullable = false)
    private WishlistName name;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.NONE)
    private List<WishlistAccommodation> accommodations = new ArrayList<>();

    public static Wishlist create(Long memberId, String name) {
        return new Wishlist(memberId, new WishlistName(name));
    }

    private Wishlist(Long memberId, WishlistName name) {
        this.memberId = memberId;
        this.name = name;
    }

    public void updateName(String newName) {
        this.name = new WishlistName(newName);
    }

    public void addAccommodation(Long accommodationId) {
        if (containsAccommodation(accommodationId)) {
            return;
        }

        accommodations.add(WishlistAccommodation.create(this, accommodationId));
    }

    private boolean containsAccommodation(Long accommodationId) {
        return accommodations.stream()
                             .anyMatch(wishlistAccommodation -> wishlistAccommodation.isAccommodation(accommodationId));
    }

    public void removeAccommodation(Long accommodationId) {
        boolean removed = accommodations.removeIf(wishlistAccommodation -> wishlistAccommodation.isAccommodation(accommodationId));
        if (!removed) {
            throw WishlistExceptions.notFoundWishlistAccommodation(id, accommodationId, memberId);
        }
    }

    public void updateMemo(Long accommodationId, String memo) {
        findAccommodation(accommodationId).updateMemo(memo);
    }

    public String getName() {
        return name.value();
    }

    private WishlistAccommodation findAccommodation(Long accommodationId) {
        return accommodations.stream()
                             .filter(wishlistAccommodation -> wishlistAccommodation.isAccommodation(accommodationId))
                             .findFirst()
                             .orElseThrow(() -> WishlistExceptions.notFoundWishlistAccommodation(id, accommodationId, memberId));
    }
}
