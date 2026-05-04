package project.accommodation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accommodation_details")
class AccommodationDetail {

    @Id
    @Column(name = "accommodation_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_people")
    private Capacity maxPeople;

    @Embedded
    private StayTimePolicy stayTimePolicy;

    @Column(name = "number")
    private String number;

    @Getter
    @Column(name = "refund_regulation", columnDefinition = "TEXT")
    private String refundRegulation;

    AccommodationDetail(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    void update(
            String description,
            Integer maxPeople,
            String checkIn,
            String checkOut,
            String number,
            String refundRegulation
    ) {
        this.description = description;
        this.maxPeople = maxPeople == null ? null : new Capacity(maxPeople);
        this.stayTimePolicy = new StayTimePolicy(checkIn, checkOut);
        this.number = number;
        this.refundRegulation = refundRegulation;
    }
}
