package project.accommodation.sync.adapter.out.persistence;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.adapter.out.persistence.AccommodationRepository;
import project.accommodation.domain.Accommodation;
import project.accommodation.sync.adapter.out.persistence.model.AccommodationModifiedTimeRow;
import project.amenity.adapter.out.persistence.AmenityRepository;
import project.amenity.domain.Amenity;
import project.area.adapter.out.persistence.AreaCodeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationSyncPersistenceAdapter {

    private final AmenityRepository amenityRepository;
    private final AreaCodeRepository areaCodeRepository;
    private final AccommodationRepository accommodationRepository;

    public Optional<Accommodation> findByContentId(String contentId) {
        return accommodationRepository.findByContentId(contentId);
    }

    public Accommodation findByContentIdOrCreate(String contentId) {
        return findByContentId(contentId).orElseGet(Accommodation::createEmpty);
    }

    public void validateAreaCode(String code) {
        if (!areaCodeRepository.existsById(code)) {
            throw new EntityNotFoundException("Cannot found AreaCode: " + code);
        }
    }

    public Amenity findAmenityByName(String amenityName) {
        return amenityRepository.findByName(amenityName)
                                .orElseThrow(() -> new EntityNotFoundException("Cannot found Amenity: " + amenityName));
    }

    public Map<String, LocalDateTime> findModifiedTimesByContentIdIn(List<String> contentIds) {
        return accommodationRepository.findModifiedTimesByContentIdIn(contentIds)
                                      .stream()
                                      .collect(toMap(
                                              AccommodationModifiedTimeRow::getContentId,
                                              AccommodationModifiedTimeRow::getModifiedTime
                                      ));
    }

    @Transactional
    public void saveAll(List<Accommodation> accommodations) {
        long start = System.currentTimeMillis();

        accommodationRepository.saveAll(accommodations);

        long end = System.currentTimeMillis();
        log.debug("총 데이터 {}개 = 숙소 {}개 저장 {}ms 소요",
                accommodations.size(),
                accommodations.size(),
                (end - start));
    }
}
