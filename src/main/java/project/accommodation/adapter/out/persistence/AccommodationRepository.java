package project.accommodation.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.accommodation.adapter.out.persistence.model.AmenityDataRow;
import project.accommodation.adapter.out.persistence.model.ImageDataRow;
import project.accommodation.domain.Accommodation;
import project.accommodation.sync.adapter.out.persistence.model.AccommodationModifiedTimeRow;
import project.common.domain.DayType;
import project.common.domain.Season;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    @Query("select a.contentId as contentId, a.modifiedTime as modifiedTime from Accommodation a where a.contentId in :contentIds")
    List<AccommodationModifiedTimeRow> findModifiedTimesByContentIdIn(@Param("contentIds") List<String> contentIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Accommodation a where a.id = :id")
    Optional<Accommodation> findByIdWithPessimisticLock(@Param("id") Long id);

    List<Accommodation> findAllByContentIdIn(Collection<String> contentIds);

    @Query("""
            select ai.imageUrl
            from AccommodationImage ai
            where ai.accommodation.id = :accommodationId
              and ai.thumbnail = true
            """)
    Optional<String> findThumbnailUrlByAccommodationId(@Param("accommodationId") Long accommodationId);

    @Query("""
            select :accommodationId as accommodationId, ai.thumbnail as thumbnail, ai.imageUrl as imageUrl
            from AccommodationImage ai
            where ai.accommodation.id = :accommodationId
            """)
    List<ImageDataRow> findImagesByAccommodationId(@Param("accommodationId") Long accommodationId);

    @Query("""
            select ai.accommodation.id as accommodationId, ai.thumbnail as thumbnail, ai.imageUrl as imageUrl
            from AccommodationImage ai
            where ai.accommodation.id in :accommodationIds
            """)
    List<ImageDataRow> findImagesByAccommodationIdIn(@Param("accommodationIds") List<Long> accommodationIds);

    @Query("""
            select :accommodationId as accommodationId, am.description
            from AccommodationAmenity aa
            join Amenity am on am.id = aa.amenityId
            where aa.accommodation.id = :accommodationId
            """)
    List<AmenityDataRow> findAmenitiesByAccommodationId(@Param("accommodationId") Long accommodationId);

    @Query("""
            select aa.accommodation.id as accommodationId, am.description as description
            from AccommodationAmenity aa
            join Amenity am on am.id = aa.amenityId
            where aa.accommodation.id in :accommodationIds
            """)
    List<AmenityDataRow> findAmenitiesByAccommodationIdIn(@Param("accommodationIds") List<Long> accommodationIds);

    @Query("""
            select ap.price
            from AccommodationPrice ap
            where ap.accommodation.id = :accommodationId
              and ap.season = :season
              and ap.dayType = :dayType
            """)
    Integer findPrice(
            @Param("accommodationId") Long accommodationId,
            @Param("season") Season season,
            @Param("dayType") DayType dayType
    );
}
