package project.chat.adapter.out.redis;

import project.common.adapter.out.persistence.repository.RedisPersistenceRepository;
import org.springframework.data.repository.CrudRepository;
import project.chat.adapter.out.redis.model.ChatRequestDocument;

import java.util.List;

@RedisPersistenceRepository
public interface ChatRequestRepository extends CrudRepository<ChatRequestDocument, String> {
    List<ChatRequestDocument> findBySenderId(Long senderId);

    List<ChatRequestDocument> findByReceiverId(Long receiverId);
}
