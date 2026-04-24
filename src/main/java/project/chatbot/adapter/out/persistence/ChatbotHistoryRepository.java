package project.chatbot.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import project.chatbot.domain.ChatbotHistory;

import java.util.List;

public interface ChatbotHistoryRepository extends JpaRepository<ChatbotHistory, Long> {
    List<ChatbotHistory> findAllByConversationId(String conversationId);
}