package project.chat.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import project.chat.application.out.command.ChatRoomStatePort;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatRoomStateAdapter implements ChatRoomStatePort {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public int loadUnreadCount(Long roomId, Long memberId) {
        Object count = stringRedisTemplate.opsForHash().get(ChatRedisKey.UNREAD.format(roomId), memberId.toString());
        return count == null ? 0 : Integer.parseInt(count.toString());
    }

    @Override
    public void incrementUnreadCount(Long roomId, Long memberId) {
        stringRedisTemplate.opsForHash().increment(ChatRedisKey.UNREAD.format(roomId), memberId.toString(), 1);
    }

    @Override
    public void removeRoomMember(Long roomId, Long memberId) {
        stringRedisTemplate.opsForSet().remove(ChatRedisKey.ROOM_MEMBERS.format(roomId), memberId.toString());
    }

    @Override
    public void addRoomMembers(Long roomId, Long... memberIds) {
        String[] ids = Arrays.stream(memberIds)
                             .map(String::valueOf)
                             .toArray(String[]::new);
        String key = ChatRedisKey.ROOM_MEMBERS.format(roomId);
        stringRedisTemplate.opsForSet().add(key, ids);
        stringRedisTemplate.expire(key, Duration.ofDays(1));
    }

    @Override
    public void resetUnreadCount(Long roomId, Long memberId) {
        stringRedisTemplate.opsForHash().put(ChatRedisKey.UNREAD.format(roomId), memberId.toString(), "0");
    }

    @Override
    public boolean isRoomMember(Long roomId, Long memberId) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet()
                                                      .isMember(ChatRedisKey.ROOM_MEMBERS.format(roomId), memberId.toString()));
    }

    @Override
    public boolean existsRoomMembers(Long roomId) {
        return stringRedisTemplate.hasKey(ChatRedisKey.ROOM_MEMBERS.format(roomId));
    }

    @Override
    public Set<Long> loadRoomMemberIds(Long roomId) {
        Set<String> members = stringRedisTemplate.opsForSet().members(ChatRedisKey.ROOM_MEMBERS.format(roomId));
        if (members == null) {
            return Set.of();
        }

        return members.stream()
                      .filter(Objects::nonNull)
                      .map(Long::valueOf)
                      .collect(Collectors.toUnmodifiableSet());
    }
}
