package project.chat.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import project.chat.adapter.in.websocket.response.ChatMessageResponse;
import project.chat.adapter.out.redis.model.ChatRedisKey;
import project.chat.application.out.command.ChatMessageDeliveryPort;
import project.chat.application.out.command.model.ChatMessagePayload;
import project.infrastructure.messaging.RedisMessagePublisher;

@Component
@RequiredArgsConstructor
public class ChatMessageDeliveryAdapter implements ChatMessageDeliveryPort {

    private static final long MESSAGE_CACHE_LIMIT = 100L;

    private final ChannelTopic chatTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessagePublisher redisMessagePublisher;

    @Override
    public void deliver(ChatMessagePayload message) {
        ChatMessageResponse response = toResponse(message);

        String queueKey = ChatRedisKey.MESSAGE_QUEUE.getTemplate();
        String cacheKey = ChatRedisKey.MESSAGE_CACHE.format(message.roomId());

        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.opsForList().rightPush(queueKey, response);
                operations.opsForList().leftPush(cacheKey, response);
                operations.opsForList().trim(cacheKey, 0L, MESSAGE_CACHE_LIMIT - 1);
                return null;
            }
        });

        redisMessagePublisher.publish(chatTopic.getTopic(), response);
    }

    private ChatMessageResponse toResponse(ChatMessagePayload message) {
        return new ChatMessageResponse(
                message.messageId(),
                message.roomId(),
                message.senderId(),
                message.senderName(),
                message.content(),
                message.timestamp(),
                message.left()
        );
    }
}
