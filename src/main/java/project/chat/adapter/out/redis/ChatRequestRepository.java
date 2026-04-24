package project.chat.adapter.out.redis;

import org.springframework.data.repository.CrudRepository;
import project.chat.adapter.out.redis.model.ChatRequest;

import java.util.List;

public interface ChatRequestRepository extends CrudRepository<ChatRequest, String> {
    List<ChatRequest> findBySenderId(Long senderId);

    List<ChatRequest> findByReceiverId(Long receiverId);
}
