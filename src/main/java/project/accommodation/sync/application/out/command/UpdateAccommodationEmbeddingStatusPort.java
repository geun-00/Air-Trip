package project.accommodation.sync.application.out.command;

import java.util.List;

public interface UpdateAccommodationEmbeddingStatusPort {
    void updateEmbeddingStatus(List<Long> ids, boolean embedded);
}
