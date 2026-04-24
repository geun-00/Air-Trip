package project.accommodation.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import project.accommodation.domain.Accommodation;
import project.accommodation.domain.AccommodationPrice;

import java.util.List;

public interface AccommodationPriceRepository extends JpaRepository<AccommodationPrice, Long> {
    @Modifying(clearAutomatically = true)
    void deleteByAccommodationIn(List<Accommodation> accommodations);
}