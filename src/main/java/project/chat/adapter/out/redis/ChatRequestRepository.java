package project.chat.adapter.out.redis;

import org.springframework.data.repository.CrudRepository;
import project.chat.adapter.out.redis.model.ChatRequestDocument;

import java.util.List;

public interface ChatRequestRepository extends CrudRepository<ChatRequestDocument, String> {
    List<ChatRequestDocument> findBySenderId(Long senderId);

    List<ChatRequestDocument> findByReceiverId(Long receiverId);
}
