package project.wishlist.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.adapter.out.persistence.BaseEntity;
import project.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wishlists")
public class Wishlist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    public static Wishlist create(Member member, String name) {
        return new Wishlist(member, name);
    }

    private Wishlist(Member member, String name) {
        this.member = member;
        this.name = name;
    }

    public void updateName(String newName) {
        this.name = newName;
    }
}