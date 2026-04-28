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
    public Accommodation loadAccommodationWithLock(Long accommodationId) {
        return accommodationRepository.findByIdWithPessimisticLock(accommodationId)
                                      .orElseThrow(() -> AccommodationExceptions.notFoundById(accommodationId));
    }
}
