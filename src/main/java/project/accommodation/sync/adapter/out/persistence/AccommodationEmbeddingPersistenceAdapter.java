package project.accommodation.sync.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.adapter.out.persistence.AccommodationRepository;
import project.accommodation.adapter.out.persistence.model.AmenityDataRow;
import project.accommodation.sync.application.model.AccommodationEmbeddingRow;
import project.accommodation.sync.application.out.command.UpdateAccommodationEmbeddingStatusPort;
import project.accommodation.sync.application.out.query.LoadAccommodationEmbeddingDataPort;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationEmbeddingPersistenceAdapter implements LoadAccommodationEmbeddingDataPort,
                                                                 UpdateAccommodationEmbeddingStatusPort {

    private final AccommodationRepository accommodationRepository;

    @Override
    public List<Long> getEmbeddingTargetIds(Pageable pageable) {
        return accommodationRepository.findEmbeddingTargetIds(pageable);
    }

    @Override
    public List<AccommodationEmbeddingRow> getEmbeddingRows(List<Long> ids) {
        return accommodationRepository.findEmbeddingRowsByIdIn(ids);
    }

    @Override
    public List<AmenityDataRow> getAmenityRows(List<Long> ids) {
        return accommodationRepository.findAmenitiesByAccommodationIdIn(ids);
    }

    @Override
    @Transactional
    public void updateEmbeddingStatus(List<Long> ids, boolean embedded) {
        accommodationRepository.updateEmbeddedByIdIn(ids, embedded);
    }
}
