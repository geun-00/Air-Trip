package project.chat.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import project.chat.adapter.in.websocket.response.ChatMessageResponse;

@Service
@RequiredArgsConstructor
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChatMessageResponse message) {
        redisTemplate.convertAndSend("chatTopic", message);
    }
}
