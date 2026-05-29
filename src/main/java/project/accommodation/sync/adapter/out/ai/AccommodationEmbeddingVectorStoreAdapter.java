package project.accommodation.sync.adapter.out.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.sync.application.model.AccommodationEmbeddingDocument;
import project.accommodation.sync.application.out.command.SaveAccommodationEmbeddingPort;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccommodationEmbeddingVectorStoreAdapter implements SaveAccommodationEmbeddingPort {

    private final VectorStore vectorStore;

    @Override
    @Transactional
    public void saveAll(List<AccommodationEmbeddingDocument> documents) {
        vectorStore.add(documents.stream()
                                 .map(document -> Document.builder()
                                                          .text(document.content())
                                                          .metadata(document.metadata())
                                                          .build())
                                 .toList());
    }
}
