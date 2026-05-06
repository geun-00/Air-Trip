package project.chatbot.adapter.out.persistence;

import project.common.adapter.out.persistence.repository.JpaPersistenceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import project.chatbot.domain.ChatbotHistory;

import java.util.List;

@JpaPersistenceRepository
public interface ChatbotHistoryRepository extends JpaRepository<ChatbotHistory, Long> {
    List<ChatbotHistory> findAllByConversationId(String conversationId);
}