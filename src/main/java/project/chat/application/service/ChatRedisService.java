package project.chat.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import project.chat.adapter.in.websocket.response.ChatMessageResponse;
import project.infrastructure.messaging.RedisMessagePublisher;

import java.util.List;
import java.util.Set;

import static project.chat.adapter.out.redis.ChatRedisKey.*;

@Service
public class ChatRedisService {

    private final ChannelTopic chatTopic;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate strRedisTemplate;
    private final RedisMessagePublisher redisMessagePublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    public ChatRedisService(@Qualifier("redisObjMapper") ObjectMapper objectMapper,
                            RedisMessagePublisher redisMessagePublisher,
                            ChannelTopic chatTopic,
                            StringRedisTemplate strRedisTemplate,
                            RedisTemplate<String, Object> redisTemplate) {
        this.objectMapper = objectMapper;
        this.redisMessagePublisher = redisMessagePublisher;
        this.chatTopic = chatTopic;
        this.strRedisTemplate = strRedisTemplate;
        this.redisTemplate = redisTemplate;
    }

    protected void incrementUnreadCount(Long roomId, Long memberId) {
        String key = UNREAD.format(roomId);
        strRedisTemplate.opsForHash().increment(key, memberId.toString(), 1);
    }

    protected void addMessageToQueueAndCache(Long roomId, ChatMessageResponse message) {
        redisTemplate.opsForList().rightPush(MESSAGE_QUEUE.getTemplate(), message);

        String cacheKey = MESSAGE_CACHE.format(roomId);
        redisTemplate.opsForList().leftPush(cacheKey, message);
        redisTemplate.opsForList().trim(cacheKey, 0, 99);
    }

    protected List<Object> getCachedRaw(Long roomId) {
        String cacheKey = MESSAGE_CACHE.format(roomId);
        return redisTemplate.opsForList().range(cacheKey, 0, -1);
    }

    protected ChatMessageResponse convert(Object obj) {
        return objectMapper.convertValue(obj, ChatMessageResponse.class);
    }

    protected void publish(ChatMessageResponse responseDto) {
        redisMessagePublisher.publish(chatTopic.getTopic(), responseDto);
    }

    protected void addMembers(Long roomId, String... memberIds) {
        String key = ROOM_MEMBERS.format(roomId);
        strRedisTemplate.opsForSet().add(key, memberIds);
        strRedisTemplate.expire(key, java.time.Duration.ofDays(1));
    }

    protected void removeMember(Long roomId, Long memberId) {
        String key = ROOM_MEMBERS.format(roomId);
        strRedisTemplate.opsForSet().remove(key, memberId.toString());
    }

    protected Boolean isMember(Long roomId, Long memberId) {
        String key = ROOM_MEMBERS.format(roomId);
        return strRedisTemplate.opsForSet().isMember(key, memberId.toString());
    }

    protected boolean hasRoomMembersKey(Long roomId) {
        return strRedisTemplate.hasKey(ROOM_MEMBERS.format(roomId));
    }

    protected Set<String> getRoomMembers(Long roomId) {
        return strRedisTemplate.opsForSet().members(ROOM_MEMBERS.format(roomId));
    }

    protected int getUnreadCount(Long roomId, Long memberId) {
        String key = UNREAD.format(roomId);
        Object count = strRedisTemplate.opsForHash().get(key, memberId.toString());
        return (count != null) ? Integer.parseInt(count.toString()) : 0;
    }

    protected void resetUnreadCount(Long roomId, Long memberId) {
        String key = UNREAD.format(roomId);
        strRedisTemplate.opsForHash().put(key, memberId.toString(), "0");
    }
}
