package project.accommodation.sync.adapter.out.persistence;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.adapter.out.persistence.AccommodationRepository;
import project.accommodation.domain.Accommodation;
import project.amenity.adapter.out.persistence.AmenityRepository;
import project.amenity.domain.Amenity;
import project.area.adapter.out.persistence.SigunguCodeRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourRepositoryFacadeManager {

    private final JdbcTemplate jdbcTemplate;
    private final AmenityRepository amenityRepository;
    private final SigunguCodeRepository sigunguCodeRepository;
    private final AccommodationRepository accommodationRepository;

    public Optional<Accommodation> findAccByContentId(String contentId) {
        return accommodationRepository.findByContentId(contentId);
    }

    public void validateSigunguCode(String code) {
        if (!sigunguCodeRepository.existsById(code)) {
            throw new EntityNotFoundException("Cannot found SigunguCode: " + code);
        }
    }

    public Amenity findAmenityByName(String amenityName) {
        return amenityRepository.findByName(amenityName)
                                .orElseThrow(() -> new EntityNotFoundException("Cannot found Amenity: " + amenityName));
    }

    public Map<String, Accommodation> findByContentIdInToMap(List<String> contentIds) {
        return accommodationRepository.findByContentIdIn(contentIds)
                                      .stream()
                                      .collect(Collectors.toMap(Accommodation::getContentId, Function.identity()));
    }

    @Transactional
    public void saveEntities(List<Accommodation> accommodations) {
        long start = System.currentTimeMillis();

        accommodationRepository.saveAll(accommodations);

        long end = System.currentTimeMillis();
        int total = accommodations.size();
        log.debug("총 데이터 {}개 = 숙소 {}개 저장 {}ms 소요",
                total,
                accommodations.size(),
                (end - start));
    }
}
