package project.accommodation.sync.application.out.command;

import project.accommodation.sync.application.model.AccommodationEmbeddingDocument;

import java.util.List;

public interface SaveAccommodationEmbeddingPort {
    void saveAll(List<AccommodationEmbeddingDocument> documents);
}
