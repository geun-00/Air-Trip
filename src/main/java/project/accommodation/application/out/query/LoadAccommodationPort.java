package project.accommodation.application.out.query;

import project.accommodation.domain.Accommodation;

import java.util.List;
import java.util.Map;

public interface LoadAccommodationPort {

    Accommodation loadAccommodation(Long accommodationId);

    Accommodation loadAccommodationWithLock(Long accommodationId);

    String loadThumbnailUrl(Long accommodationId);

    Map<Long, String> loadThumbnailUrls(List<Long> accommodationIds);

    boolean existsAccommodation(Long accommodationId);
}
