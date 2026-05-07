package project.accommodation.sync.application.out.query;

import org.springframework.data.domain.Pageable;
import project.accommodation.adapter.out.persistence.model.AmenityDataRow;
import project.accommodation.sync.application.model.AccommodationEmbeddingRow;

import java.util.List;

public interface LoadAccommodationEmbeddingDataPort {
    List<Long> getEmbeddingTargetIds(Pageable pageable);

    List<AccommodationEmbeddingRow> getEmbeddingRows(List<Long> ids);

    List<AmenityDataRow> getAmenityRows(List<Long> ids);
}
