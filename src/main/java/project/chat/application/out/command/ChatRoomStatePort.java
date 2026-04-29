package project.chat.application.out.command;

public interface ChatRoomStatePort {

    int loadUnreadCount(Long roomId, Long memberId);

    void resetUnreadCount(Long roomId, Long memberId);

    void removeRoomMember(Long roomId, Long memberId);
}
