package project.accommodation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.domain.DayType;
import project.common.domain.Season;
import project.common.adapter.out.persistence.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accommodation_prices")
public class AccommodationPrice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public static AccommodationPrice create(Accommodation accommodation, Season season, DayType dayType, Integer price) {
        return new AccommodationPrice(accommodation, season, dayType, price);
    }

    private AccommodationPrice(Accommodation accommodation, Season season, DayType dayType, Integer price) {
        this.accommodation = accommodation;
        this.season = season;
        this.dayType = dayType;
        this.price = price;
    }
}