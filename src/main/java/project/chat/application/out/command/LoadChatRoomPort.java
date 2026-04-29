package project.chat.application.out.command;

import project.chat.application.out.query.model.ChatRoomInfoView;
import project.chat.domain.ChatRoom;

public interface LoadChatRoomPort {

    ChatRoom loadParticipantChatRoom(Long roomId, Long memberId);

    ChatRoomInfoView loadChatRoomInfo(Long currentMemberId, Long otherMemberId, Long roomId);

    int loadUnreadCount(Long roomId, Long memberId);

    void markLatestMessageAsRead(Long roomId, ChatRoom chatRoom, Long memberId);

    void removeRoomMember(Long roomId, Long memberId);

    void resetUnreadCount(Long roomId, Long memberId);
}
