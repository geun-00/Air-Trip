package project.reservation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.adapter.out.persistence.BaseEntity;
import project.reservation.domain.exception.ReservationExceptions;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservations")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "accommodation_id", nullable = false)
    private Long accommodationId;

    @Embedded
    private GuestCount guestCount;

    @Embedded
    private StayPeriod stayPeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    public static Reservation createPending(
            Long memberId,
            Long accommodationId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int adults,
            int children,
            int infants
    ) {
        return new Reservation(
                memberId,
                accommodationId,
                GuestCount.of(adults, children, infants),
                StayPeriod.of(startDate, endDate),
                ReservationStatus.PENDING
        );
    }

    private Reservation(
            Long memberId,
            Long accommodationId,
            GuestCount guestCount,
            StayPeriod stayPeriod,
            ReservationStatus status
    ) {
        this.memberId = memberId;
        this.accommodationId = accommodationId;
        this.guestCount = guestCount;
        this.stayPeriod = stayPeriod;
        this.status = status;
    }

    public void confirm() {
        if (this.status != ReservationStatus.PENDING) {
            throw ReservationExceptions.alreadyConfirmed(this.id);
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public void validateOwner(Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw ReservationExceptions.notOwner(this.id, memberId);
        }
    }

    public LocalDateTime getStartDate() {
        return stayPeriod.getStartDate();
    }

    public LocalDateTime getEndDate() {
        return stayPeriod.getEndDate();
    }

    public LocalDateTime getDisplayEndDate() {
        return stayPeriod.getDisplayEndDate();
    }

    public int getAdults() {
        return guestCount.getAdults();
    }

    public int getChildren() {
        return guestCount.getChildren();
    }

    public int getInfants() {
        return guestCount.getInfants();
    }
}
