package project.chat.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import project.chat.adapter.out.persistence.model.ChatRoomInfoRow;
import project.chat.adapter.out.redis.ChatRedisKey;
import project.chat.application.out.command.LoadChatRoomPort;
import project.chat.application.out.query.model.ChatRoomInfoView;
import project.chat.domain.ChatRoom;
import project.chat.domain.exception.ChatExceptions;

@Repository
@RequiredArgsConstructor
public class ChatRoomCommandPersistenceAdapter implements LoadChatRoomPort {

    private final ChatRoomRepository chatRoomRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomQueryRepository chatRoomQueryRepository;

    @Override
    public ChatRoom loadParticipantChatRoom(Long roomId, Long memberId) {
        return chatRoomRepository.findByIdAndMemberIdWithParticipants(roomId, memberId)
                                 .orElseThrow(() -> ChatExceptions.notFoundChatParticipant(roomId, memberId));
    }

    @Override
    public ChatRoomInfoView loadChatRoomInfo(Long currentMemberId, Long otherMemberId, Long roomId) {
        return chatRoomQueryRepository.findChatRoomInfo(currentMemberId, otherMemberId, roomId)
                                      .map(this::toView)
                                      .orElseThrow(() -> ChatExceptions.notFoundChatRoom(currentMemberId, otherMemberId));
    }

    private ChatRoomInfoView toView(ChatRoomInfoRow row) {
        return new ChatRoomInfoView(
                row.roomId(),
                row.customRoomName().value(),
                row.memberId(),
                row.memberName(),
                row.memberProfileImage(),
                row.otherMemberActive(),
                row.lastMessage() == null ? null : row.lastMessage().value(),
                row.lastMessageTime()
        );
    }

    @Override
    public int loadUnreadCount(Long roomId, Long memberId) {
        Object count = stringRedisTemplate.opsForHash().get(ChatRedisKey.UNREAD.format(roomId), memberId.toString());
        return count == null ? 0 : Integer.parseInt(count.toString());
    }

    @Override
    public void markLatestMessageAsRead(Long roomId, ChatRoom chatRoom, Long memberId) {
        chatMessageRepository.findLatestByChatRoomId(roomId)
                             .ifPresent(message -> chatRoom.updateLastReadMessage(memberId, message.getId()));
    }

    @Override
    public void removeRoomMember(Long roomId, Long memberId) {
        stringRedisTemplate.opsForSet().remove(ChatRedisKey.ROOM_MEMBERS.format(roomId), memberId.toString());
    }
}
