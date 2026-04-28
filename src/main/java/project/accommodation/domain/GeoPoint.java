package project.accommodation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GeoPoint {

    @Column(name = "map_x", nullable = false)
    private Double longitude;

    @Column(name = "map_y", nullable = false)
    private Double latitude;

    public GeoPoint(Double longitude, Double latitude) {
        if (longitude == null || latitude == null) {
            throw new IllegalArgumentException("geo point must have both coordinates");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("longitude must be between -180 and 180");
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("latitude must be between -90 and 90");
        }

        this.longitude = longitude;
        this.latitude = latitude;
    }
}
