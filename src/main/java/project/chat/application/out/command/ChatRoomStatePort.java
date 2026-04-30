package project.chat.application.out.command;

public interface ChatRoomStatePort {

    int loadUnreadCount(Long roomId, Long memberId);

    void incrementUnreadCount(Long roomId, Long memberId);

    void resetUnreadCount(Long roomId, Long memberId);

    void removeRoomMember(Long roomId, Long memberId);

    void addRoomMembers(Long roomId, Long... memberIds);

    boolean isRoomMember(Long roomId, Long memberId);

    boolean existsRoomMembers(Long roomId);

    java.util.Set<Long> loadRoomMemberIds(Long roomId);
}
