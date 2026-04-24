package project.chat.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import project.chat.domain.ChatMessage;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findFirstByChatRoomIdOrderByIdDesc(Long roomId);
}