package project.chat.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.chat.domain.ChatMessage;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoomId = :roomId ORDER BY m.id DESC LIMIT 1")
    Optional<ChatMessage> findLatestByChatRoomId(@Param("roomId") Long roomId);
}