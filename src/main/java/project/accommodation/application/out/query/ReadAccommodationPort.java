package project.accommodation.application.out.query;

import project.accommodation.domain.Accommodation;

import java.util.List;
import java.util.Map;

public interface ReadAccommodationPort {

    Accommodation getById(Long accommodationId);

    Accommodation getByIdWithLock(Long accommodationId);

    String getThumbnailUrlById(Long accommodationId);

    Map<Long, String> getThumbnailUrlsByIds(List<Long> accommodationIds);

    boolean existsById(Long accommodationId);
}
