package project.chatbot.adapter.out.mongo;

import project.common.adapter.out.persistence.repository.MongoPersistenceRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@MongoPersistenceRepository
public interface ChatbotHistoryMongoRepository extends MongoRepository<ChatbotHistoryDocument, String> {
    List<ChatbotHistoryDocument> findByConversationIdOrderByCreatedAtAsc(String conversationId);
}
