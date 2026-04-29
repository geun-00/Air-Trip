package project.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.domain.Rating;
import project.common.adapter.out.persistence.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "rating", nullable = false)
    private Rating rating;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    public static Review create(
            double rating,
            String content,
            Long reservationId,
            Long memberId
    ) {
        return new Review(content, new Rating(rating), reservationId, memberId);
    }

    private Review(String content, Rating rating, Long reservationId, Long memberId) {
        this.content = content;
        this.rating = rating;
        this.reservationId = reservationId;
        this.memberId = memberId;
    }

    public void update(double rating, String content) {
        this.rating = new Rating(rating);
        this.content = content;
    }
}
