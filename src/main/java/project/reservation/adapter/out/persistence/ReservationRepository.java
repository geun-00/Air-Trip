package project.reservation.adapter.out.persistence;

import project.common.adapter.out.persistence.repository.JpaPersistenceRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.reservation.domain.Reservation;

import java.util.Optional;

@JpaPersistenceRepository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.id = :id")
    Optional<Reservation> findByIdWithPessimisticLock(@Param("id") Long id);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select r from Reservation r where r.id = :id")
    Optional<Reservation> findByIdWithOptimisticLock(@Param("id") Long id);
}
