package project.accommodation.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.accommodation.application.out.query.LoadAccommodationPort;
import project.accommodation.domain.Accommodation;
import project.accommodation.domain.exception.AccommodationExceptions;

@Repository
@RequiredArgsConstructor
public class AccommodationPersistenceAdapter implements LoadAccommodationPort {

    private final AccommodationRepository accommodationRepository;

    @Override
    public Accommodation loadAccommodation(Long accommodationId) {
        return accommodationRepository.findById(accommodationId)
                                      .orElseThrow(() -> AccommodationExceptions.notFoundById(accommodationId));
    }

    @Override
    public Accommodation loadAccommodationWithLock(Long accommodationId) {
        return accommodationRepository.findByIdWithPessimisticLock(accommodationId)
                                      .orElseThrow(() -> AccommodationExceptions.notFoundById(accommodationId));
    }

    @Override
    public String loadThumbnailUrl(Long accommodationId) {
        return accommodationRepository.findThumbnailUrlByAccommodationId(accommodationId)
                                      .orElse(null);
    }

    @Override
    public boolean existsAccommodation(Long accommodationId) {
        return accommodationRepository.existsById(accommodationId);
    }
}
