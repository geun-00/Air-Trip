package project.wishlist.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.adapter.out.persistence.BaseEntity;

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

    public String getName() {
        return name.value();
    }
}
