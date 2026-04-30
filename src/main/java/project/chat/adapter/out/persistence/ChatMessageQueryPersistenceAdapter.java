package project.chat.adapter.out.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import project.chat.adapter.in.websocket.response.ChatMessageResponse;
import project.chat.adapter.out.persistence.model.ChatMessageHistoryRow;
import project.chat.adapter.out.persistence.repository.ChatMessageQueryRepository;
import project.chat.adapter.out.redis.model.ChatRedisKey;
import project.chat.application.out.query.LoadChatMessagesPort;
import project.chat.application.out.query.model.ChatMessageHistoryView;

import java.util.List;

@Repository
public class ChatMessageQueryPersistenceAdapter implements LoadChatMessagesPort {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatMessageQueryRepository chatMessageQueryRepository;

    public ChatMessageQueryPersistenceAdapter(
            @Qualifier("redisObjMapper") ObjectMapper objectMapper,
            RedisTemplate<String, Object> redisTemplate,
            ChatMessageQueryRepository chatMessageQueryRepository
    ) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
        this.chatMessageQueryRepository = chatMessageQueryRepository;
    }

    @Override
    public List<ChatMessageHistoryView> loadMessages(
            Long lastMessageId,
            Long roomId,
            int pageSize
    ) {
        return chatMessageQueryRepository.getMessages(lastMessageId, roomId, pageSize)
                                         .stream()
                                         .map(this::toView)
                                         .toList();
    }

    private ChatMessageHistoryView toView(ChatMessageHistoryRow row) {
        return new ChatMessageHistoryView(
                String.valueOf(row.messageId()),
                row.roomId(),
                row.senderId(),
                row.senderName().value(),
                row.content().value(),
                row.timestamp(),
                false
        );
    }

    @Override
    public List<ChatMessageHistoryView> loadCachedMessages(Long roomId) {
        List<Object> cachedMessages = redisTemplate.opsForList().range(ChatRedisKey.MESSAGE_CACHE.format(roomId), 0, -1);
        if (cachedMessages == null || cachedMessages.isEmpty()) {
            return List.of();
        }

        return cachedMessages.stream()
                             .map(message -> objectMapper.convertValue(message, ChatMessageResponse.class))
                             .map(this::toView)
                             .toList();
    }

    private ChatMessageHistoryView toView(ChatMessageResponse response) {
        return new ChatMessageHistoryView(
                response.getMessageId(),
                response.getRoomId(),
                response.getSenderId(),
                response.getSenderName(),
                response.getContent(),
                response.getTimestamp(),
                response.isLeft()
        );
    }
}
