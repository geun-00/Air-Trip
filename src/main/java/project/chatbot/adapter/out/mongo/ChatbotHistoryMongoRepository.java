package project.chatbot.adapter.out.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatbotHistoryMongoRepository extends MongoRepository<ChatbotHistoryDocument, String> {
    List<ChatbotHistoryDocument> findByConversationIdOrderByCreatedAtAsc(String conversationId);
}
