package project.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class StayPeriod {

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    public long nights() {
        return ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
    }

    public boolean overlaps(StayPeriod other) {
        return this.startDate.isBefore(other.endDate) && this.endDate.isAfter(other.startDate);
    }
}
