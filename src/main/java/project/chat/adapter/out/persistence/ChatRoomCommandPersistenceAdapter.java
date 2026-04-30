package project.chat.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.chat.adapter.out.persistence.model.ChatRoomInfoRow;
import project.chat.adapter.out.persistence.repository.ChatMessageRepository;
import project.chat.adapter.out.persistence.repository.ChatRoomQueryRepository;
import project.chat.adapter.out.persistence.repository.ChatRoomRepository;
import project.chat.application.out.command.LoadChatRoomPort;
import project.chat.application.out.command.SaveChatRoomPort;
import project.chat.application.out.query.model.ChatRoomInfoView;
import project.chat.domain.ChatMessage;
import project.chat.domain.ChatRoom;
import project.chat.domain.exception.ChatExceptions;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ChatRoomCommandPersistenceAdapter implements LoadChatRoomPort, SaveChatRoomPort {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomQueryRepository chatRoomQueryRepository;

    @Override
    public ChatRoom loadParticipantChatRoom(Long roomId, Long memberId) {
        return chatRoomRepository.findByIdAndMemberIdWithParticipants(roomId, memberId)
                                 .orElseThrow(() -> ChatExceptions.notFoundChatParticipant(roomId, memberId));
    }

    @Override
    public Optional<ChatRoom> findChatRoomByMembersId(Long currentMemberId, Long otherMemberId) {
        return chatRoomRepository.findByMembersId(currentMemberId, otherMemberId);
    }

    @Override
    public Optional<Long> loadLatestMessageId(Long roomId) {
        return chatMessageRepository.findLatestByChatRoomId(roomId)
                                    .map(ChatMessage::getId);
    }

    @Override
    public ChatRoomInfoView loadChatRoomInfo(Long currentMemberId, Long otherMemberId, Long roomId) {
        return chatRoomQueryRepository.findChatRoomInfo(currentMemberId, otherMemberId, roomId)
                                      .map(this::toView)
                                      .orElseThrow(() -> ChatExceptions.notFoundChatRoom(currentMemberId, otherMemberId));
    }

    @Override
    public List<ChatRoomInfoView> loadChatRooms(Long memberId) {
        return chatRoomQueryRepository.findChatRooms(memberId)
                                      .stream()
                                      .map(this::toView)
                                      .toList();
    }

    @Override
    public Set<Long> loadParticipantIds(Long roomId) {
        return Set.copyOf(chatRoomRepository.findActiveParticipantIds(roomId));
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
    public boolean existsActiveChatRoom(Long currentMemberId, Long otherMemberId) {
        return chatRoomRepository.existsActiveChatRoom(currentMemberId, otherMemberId);
    }

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }
}
