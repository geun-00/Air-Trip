package project.chat.application.out.command;

import project.chat.application.out.query.model.ChatRoomInfoView;
import project.chat.domain.ChatRoom;

public interface LoadChatRoomPort {

    ChatRoom loadParticipantChatRoom(Long roomId, Long memberId);

    ChatRoomInfoView loadChatRoomInfo(Long currentMemberId, Long otherMemberId, Long roomId);

    int loadUnreadCount(Long roomId, Long memberId);
}
