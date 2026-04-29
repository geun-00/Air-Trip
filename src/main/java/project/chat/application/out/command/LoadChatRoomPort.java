package project.chat.application.out.command;

import project.chat.application.out.query.model.ChatRoomInfoView;
import project.chat.domain.ChatRoom;

import java.util.Optional;

public interface LoadChatRoomPort {

    ChatRoom loadParticipantChatRoom(Long roomId, Long memberId);

    Optional<Long> loadLatestMessageId(Long roomId);

    ChatRoomInfoView loadChatRoomInfo(Long currentMemberId, Long otherMemberId, Long roomId);
}
