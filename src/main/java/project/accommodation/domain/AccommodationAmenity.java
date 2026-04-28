package project.accommodation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	    name = "accommodation_amenities",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_acc_amenity",
	            columnNames = {"accommodation_id", "amenity_id"}
	        )
	    }
	)
class AccommodationAmenity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accommodation_amenities_seq")
    @SequenceGenerator(name = "accommodation_amenities_seq", sequenceName = "accommodation_amenities_seq")
    @Column(name = "accommodation_amenities_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Column(name = "amenity_id", nullable = false)
    private Long amenityId;

    static AccommodationAmenity create(Accommodation accommodation, Long amenityId) {
        return new AccommodationAmenity(accommodation, amenityId);
    }

    private AccommodationAmenity(Accommodation accommodation, Long amenityId) {
        this.accommodation = accommodation;
        this.amenityId = amenityId;
    }
}
