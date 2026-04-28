package project.amenity.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import project.amenity.domain.Amenity;

import java.util.Collection;
import java.util.List;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {

	List<Amenity> findAllByNameIn(Collection<String> names);
}
