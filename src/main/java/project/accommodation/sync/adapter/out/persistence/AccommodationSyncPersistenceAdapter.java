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
import project.area.domain.AreaCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationSyncPersistenceAdapter {

    private final AmenityRepository amenityRepository;
    private final AreaCodeRepository areaCodeRepository;
    private final AccommodationRepository accommodationRepository;

    public void validateAreaCodes(Set<String> codes) {
        List<AreaCode> found = areaCodeRepository.findAllById(codes);
        if (found.size() != codes.size()) {
            Set<String> foundCodes = found.stream()
                                          .map(AreaCode::getCode)
                                          .collect(toSet());
            Set<String> invalidCodes = codes.stream()
                                            .filter(code -> !foundCodes.contains(code))
                                            .collect(toSet());
            throw new EntityNotFoundException("Cannot found AreaCode: " + invalidCodes);
        }
    }

    public Map<String, Amenity> findAllAmenitiesByNames(Set<String> names) {
        return amenityRepository.findAllByNameIn(names)
                                .stream()
                                .collect(toMap(Amenity::getName, amenity -> amenity));
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

    public Map<String, Accommodation> findAllByContentIdIn(List<String> contentIds) {
        List<Accommodation> rows = accommodationRepository.findAllByContentIdIn(contentIds);

        return rows.stream()
                   .collect(toMap(
                           Accommodation::getContentId,
                           accommodation -> accommodation
                   ));
    }
}
