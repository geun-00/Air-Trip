package project.accommodation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import project.common.domain.DayType;
import project.common.domain.Season;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accommodation_prices")
class AccommodationPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accommodation_prices_seq")
    @SequenceGenerator(name = "accommodation_prices_seq", sequenceName = "accommodation_prices_seq")
    @Column(name = "accommodation_price_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Enumerated(EnumType.STRING)
    @Column(name = "season", nullable = false)
    private Season season;      //비수기, 성수기

    @Enumerated(EnumType.STRING)
    @Column(name = "day_type", nullable = false)
    private DayType dayType;    //주중, 주말

    @Column(name = "price", nullable = false)
    private Integer price;

    static AccommodationPrice create(Accommodation accommodation, Season season, DayType dayType, Integer price) {
        return new AccommodationPrice(accommodation, season, dayType, price);
    }

    private AccommodationPrice(Accommodation accommodation, Season season, DayType dayType, Integer price) {
        this.accommodation = accommodation;
        this.season = season;
        this.dayType = dayType;
        this.price = price;
    }
}
