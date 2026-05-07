package project.accommodation.sync.application.model;

import java.util.Map;

public record AccommodationEmbeddingDocument(
        String content,
        Map<String, Object> metadata
) {
}
