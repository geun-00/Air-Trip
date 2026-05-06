package project.amenity.adapter.out.persistence;

import project.common.adapter.out.persistence.repository.JpaPersistenceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import project.amenity.domain.Amenity;

import java.util.Collection;
import java.util.List;

@JpaPersistenceRepository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

	List<Amenity> findAllByNameIn(Collection<String> names);
}
