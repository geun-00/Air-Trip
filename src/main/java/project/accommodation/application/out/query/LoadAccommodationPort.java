package project.accommodation.application.out.query;

import project.accommodation.domain.Accommodation;

public interface LoadAccommodationPort {

    Accommodation loadAccommodationWithLock(Long accommodationId);
}
