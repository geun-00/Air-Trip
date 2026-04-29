package project.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class GuestCount {

    @Column(name = "adults", nullable = false)
    private int adults;

    @Column(name = "children", nullable = false)
    private int children;

    @Column(name = "infant", nullable = false)
    private int infants;

    public int totalGuests() {
        return adults + children + infants;
    }
}
