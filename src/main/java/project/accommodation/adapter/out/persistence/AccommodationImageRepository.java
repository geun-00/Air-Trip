package project.accommodation.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.accommodation.domain.Accommodation;
import project.accommodation.domain.AccommodationImage;

@Deprecated(forRemoval = true)
public interface AccommodationImageRepository extends JpaRepository<AccommodationImage, Long> {

    @Query("""
            SELECT ai.imageUrl
            FROM AccommodationImage AS ai
            WHERE ai.accommodation = :accommodation AND ai.thumbnail = TRUE
            """)
    String findThumbnailUrl(@Param("accommodation") Accommodation accommodation);
}
