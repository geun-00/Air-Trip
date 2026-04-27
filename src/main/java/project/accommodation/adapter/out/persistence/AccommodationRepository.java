package project.accommodation.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.accommodation.domain.Accommodation;
import project.accommodation.sync.adapter.out.persistence.model.AccommodationModifiedTimeRow;

import java.util.List;
import java.util.Optional;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    @EntityGraph(attributePaths = {"images", "prices", "amenities"})
	Optional<Accommodation> findByContentId(String tourApiId);

    List<Accommodation> findByContentIdIn(List<String> contentIds);

    @Query("select a.contentId as contentId, a.modifiedTime as modifiedTime from Accommodation a where a.contentId in :contentIds")
    List<AccommodationModifiedTimeRow> findModifiedTimesByContentIdIn(@Param("contentIds") List<String> contentIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Accommodation a where a.id = :id")
    Optional<Accommodation> findByIdWithPessimisticLock(@Param("id") Long id);
}
