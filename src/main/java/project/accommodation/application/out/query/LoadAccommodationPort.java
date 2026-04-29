package project.accommodation.application.out.query;

import project.accommodation.domain.Accommodation;

public interface LoadAccommodationPort {

    Accommodation loadAccommodation(Long accommodationId);

    Accommodation loadAccommodationWithLock(Long accommodationId);

    String loadThumbnailUrl(Long accommodationId);

    boolean existsAccommodation(Long accommodationId);
}
