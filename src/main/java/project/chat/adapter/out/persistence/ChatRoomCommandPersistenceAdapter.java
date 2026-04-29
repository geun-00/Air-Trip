package project.chat.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.chat.adapter.out.persistence.model.ChatRoomInfoRow;
import project.chat.application.out.command.LoadChatRoomPort;
import project.chat.application.out.query.model.ChatRoomInfoView;
import project.chat.domain.ChatRoom;
import project.chat.domain.exception.ChatExceptions;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomCommandPersistenceAdapter implements LoadChatRoomPort {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomQueryRepository chatRoomQueryRepository;

    @Override
    public ChatRoom loadParticipantChatRoom(Long roomId, Long memberId) {
        return chatRoomRepository.findByIdAndMemberIdWithParticipants(roomId, memberId)
                                 .orElseThrow(() -> ChatExceptions.notFoundChatParticipant(roomId, memberId));
    }

    @Override
    public Optional<Long> loadLatestMessageId(Long roomId) {
        return chatMessageRepository.findLatestByChatRoomId(roomId)
                                    .map(project.chat.domain.ChatMessage::getId);
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
}
