package project.accommodation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// TODO : package-private 수정
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accommodation_images")
public class AccommodationImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_image_id", nullable = false)
    private Long id;

    @Column(name = "image_url", nullable = false, length = 700)
    private String imageUrl;

    @Column(name = "thumbnail", nullable = false)
    private boolean thumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    static AccommodationImage thumbnailOf(Accommodation accommodation, String thumbnailUrl) {
        return new AccommodationImage(thumbnailUrl, true, accommodation);
    }

    static AccommodationImage normalOf(Accommodation accommodation, String thumbnailUrl) {
        return new AccommodationImage(thumbnailUrl, false, accommodation);
    }

    private AccommodationImage(String imageUrl, boolean thumbnail, Accommodation accommodation) {
        this.imageUrl = imageUrl;
        this.thumbnail = thumbnail;
        this.accommodation = accommodation;
    }
}
