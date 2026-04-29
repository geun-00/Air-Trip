package project.chat.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import project.chat.application.out.command.ChatRoomStatePort;

import java.util.Arrays;

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
    public void removeRoomMember(Long roomId, Long memberId) {
        stringRedisTemplate.opsForSet().remove(ChatRedisKey.ROOM_MEMBERS.format(roomId), memberId.toString());
    }

    @Override
    public void addRoomMembers(Long roomId, Long... memberIds) {
        String[] ids = Arrays.stream(memberIds)
                             .map(String::valueOf)
                             .toArray(String[]::new);
        stringRedisTemplate.opsForSet().add(ChatRedisKey.ROOM_MEMBERS.format(roomId), ids);
    }

    @Override
    public void resetUnreadCount(Long roomId, Long memberId) {
        stringRedisTemplate.opsForHash().put(ChatRedisKey.UNREAD.format(roomId), memberId.toString(), "0");
    }
}
