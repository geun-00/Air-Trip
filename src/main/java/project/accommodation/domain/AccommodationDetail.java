package project.accommodation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accommodation_details")
class AccommodationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_detail_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accommodation_id", nullable = false, unique = true)
    private Accommodation accommodation;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_people")
    private Integer maxPeople;

    @Column(name = "check_in")
    private String checkIn;

    @Column(name = "check_out")
    private String checkOut;

    @Column(name = "number")
    private String number;

    @Column(name = "refund_regulation", columnDefinition = "TEXT")
    private String refundRegulation;

    AccommodationDetail(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    void update(String description, Integer maxPeople, String checkIn, String checkOut, String number, String refundRegulation) {
        this.description = description;
        this.maxPeople = maxPeople;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.number = number;
        this.refundRegulation = refundRegulation;
    }
}
