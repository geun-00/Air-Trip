package project.accommodation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.accommodation.sync.application.model.AccommodationProcessorDto;
import project.common.adapter.out.persistence.BaseEntity;
import project.common.domain.DayType;
import project.common.domain.Season;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accommodations")
public class Accommodation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_id", nullable = false)
    private Long id;

    @Embedded
    private GeoPoint geoPoint;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "content_id", unique = true, nullable = false)
    private String contentId;

    @Column(name = "modified_time", nullable = false)
    private LocalDateTime modifiedTime;

    @Column(name = "sigungu_code", nullable = false)
    private String sigunguCode;

    @OneToOne(mappedBy = "accommodation", cascade = CascadeType.ALL, optional = false)
    private AccommodationDetail detail;

    @Column(name = "is_embedded")
    private Boolean isEmbedded;

    @Column(name = "reservation_count")
    private ReservationCount reservationCount = ReservationCount.ZERO;

    @Column(name = "average_rating")
    private Rating averageRating = Rating.ZERO;

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccommodationImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccommodationPrice> prices = new LinkedHashSet<>();

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccommodationAmenity> amenities = new LinkedHashSet<>();

    public static Accommodation createEmpty() {
        return new Accommodation();
    }

    public void updateOrInit(AccommodationProcessorDto dto) {
        this.geoPoint = new GeoPoint(dto.getMapX(), dto.getMapY());
        this.address = dto.getAddress();
        this.title = dto.getTitle();
        this.modifiedTime = dto.getModifiedTime();
        this.contentId = dto.getContentId();
        this.sigunguCode = dto.getSigunguCode();

        if (this.detail == null) {
            this.detail = new AccommodationDetail(this);
        }
        this.detail.update(dto.getDescription(), dto.getMaxPeople(), dto.getCheckIn(), dto.getCheckOut(), dto.getNumber(), dto.getRefundRegulation());
    }

    public String getRefundRegulation() {
        return detail.getRefundRegulation();
    }

    public void replaceImages(
            String thumbnailUrl,
            List<String> originImageUrls,
            List<String> roomImageUrls
    ) {
        this.images.clear();
        this.images.add(AccommodationImage.thumbnailOf(this, thumbnailUrl));
        addNormalImages(originImageUrls, thumbnailUrl);
        addNormalImages(roomImageUrls, thumbnailUrl);
    }

    private void addNormalImages(List<String> imageUrls, String thumbnailUrl) {
        imageUrls.stream()
                 .filter(imageUrl -> !imageUrl.equals(thumbnailUrl))
                 .forEach(imageUrl -> this.images.add(AccommodationImage.normalOf(this, imageUrl)));
    }

    public void replacePrices(Map<Season, Map<DayType, Integer>> prices) {
        this.prices.clear();

        for (Season season : Season.values()) {
            for (DayType dayType : DayType.values()) {
                this.prices.add(
                        AccommodationPrice.create(
                                this,
                                season,
                                dayType,
                                prices.get(season).get(dayType)
                        ));
            }
        }
    }

    public void replaceAmenities(List<Long> amenityIds) {
        this.amenities.clear();
        amenityIds.stream()
                  .distinct()
                  .map(amenityId -> AccommodationAmenity.create(this, amenityId))
                  .forEach(this.amenities::add);
    }
}
