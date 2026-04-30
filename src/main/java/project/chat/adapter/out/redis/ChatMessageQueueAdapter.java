package project.chat.adapter.out.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import project.chat.adapter.in.websocket.response.ChatMessageResponse;
import project.chat.adapter.out.redis.model.ChatRedisKey;
import project.chat.application.out.command.ChatMessageQueuePort;
import project.chat.application.out.command.model.ChatMessagePayload;

import java.util.List;

@Component
public class ChatMessageQueueAdapter implements ChatMessageQueuePort {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public ChatMessageQueueAdapter(
            @Qualifier("redisObjMapper") ObjectMapper objectMapper,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<ChatMessagePayload> loadPendingMessages() {
        if (!redisTemplate.hasKey(queueKey())) {
            return List.of();
        }

        redisTemplate.rename(queueKey(), backupKey());

        List<Object> messages = redisTemplate.opsForList().range(backupKey(), 0, -1);
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }

        return messages.stream()
                       .map(message -> objectMapper.convertValue(message, ChatMessageResponse.class))
                       .map(this::toPayload)
                       .toList();
    }

    @Override
    public void completePendingMessages() {
        redisTemplate.delete(backupKey());
    }

    private ChatMessagePayload toPayload(ChatMessageResponse response) {
        return new ChatMessagePayload(
                response.getMessageId(),
                response.getRoomId(),
                response.getSenderId(),
                response.getSenderName(),
                response.getContent(),
                response.getTimestamp(),
                response.isLeft()
        );
    }

    private String queueKey() {
        return ChatRedisKey.MESSAGE_QUEUE.getTemplate();
    }

    private String backupKey() {
        return queueKey() + ":backup";
    }
}
