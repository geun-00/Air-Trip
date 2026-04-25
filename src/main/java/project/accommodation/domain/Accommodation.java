package project.accommodation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.accommodation.sync.application.model.AccommodationProcessorDto;
import project.common.adapter.out.persistence.BaseEntity;

import java.time.LocalDateTime;

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

    public int getReservationCount() {
        return reservationCount.value();
    }
}
