package project.chat.application.out.command;

import project.chat.application.out.query.model.ChatRoomInfoView;
import project.chat.domain.ChatRoom;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LoadChatRoomPort {

    ChatRoom loadParticipantChatRoom(Long roomId, Long memberId);

    Optional<ChatRoom> findChatRoomByMembersId(Long currentMemberId, Long otherMemberId);

    Optional<Long> loadLatestMessageId(Long roomId);

    ChatRoomInfoView loadChatRoomInfo(Long currentMemberId, Long otherMemberId, Long roomId);

    List<ChatRoomInfoView> loadChatRooms(Long memberId);

    Set<Long> loadParticipantIds(Long roomId);

    boolean existsActiveChatRoom(Long currentMemberId, Long otherMemberId);
}
