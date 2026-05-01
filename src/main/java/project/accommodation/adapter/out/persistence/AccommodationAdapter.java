package project.accommodation.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.accommodation.adapter.out.persistence.model.ImageDataRow;
import project.accommodation.application.out.query.ReadAccommodationPort;
import project.accommodation.domain.Accommodation;
import project.accommodation.domain.exception.AccommodationExceptions;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class AccommodationAdapter implements ReadAccommodationPort {

    private final AccommodationRepository accommodationRepository;

    @Override
    public Accommodation getById(Long accommodationId) {
        return accommodationRepository.findById(accommodationId)
                                      .orElseThrow(() -> AccommodationExceptions.notFoundById(accommodationId));
    }

    @Override
    public Accommodation getByIdWithLock(Long accommodationId) {
        return accommodationRepository.findByIdWithPessimisticLock(accommodationId)
                                      .orElseThrow(() -> AccommodationExceptions.notFoundById(accommodationId));
    }

    @Override
    public String getThumbnailUrlById(Long accommodationId) {
        return accommodationRepository.findThumbnailUrlByAccommodationId(accommodationId)
                                      .orElse(null);
    }

    @Override
    public Map<Long, String> getThumbnailUrlsByIds(List<Long> accommodationIds) {
        if (accommodationIds == null || accommodationIds.isEmpty()) {
            return Map.of();
        }

        return accommodationRepository.findImagesByAccommodationIdIn(accommodationIds)
                                      .stream()
                                      .filter(ImageDataRow::getThumbnail)
                                      .collect(toMap(
                                              ImageDataRow::getAccommodationId,
                                              ImageDataRow::getImageUrl,
                                              (existing, ignored) -> existing
                                      ));
    }

    @Override
    public boolean existsById(Long accommodationId) {
        return accommodationRepository.existsById(accommodationId);
    }
}
