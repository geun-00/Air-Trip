package project.chat.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.chat.domain.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
                SELECT cp1.chatRoom
                FROM ChatParticipant cp1
                JOIN ChatParticipant cp2 ON cp1.chatRoom = cp2.chatRoom
                WHERE cp1.memberId = :currentMemberId
                AND cp2.memberId = :otherMemberId
            """)
    Optional<ChatRoom> findByMembersId(@Param("currentMemberId") Long currentMemberId, @Param("otherMemberId") Long otherMemberId);

    @Query("""
            SELECT DISTINCT cr
            FROM ChatRoom cr
            LEFT JOIN FETCH cr.participants
            WHERE cr.id = :roomId
            AND EXISTS (
                SELECT participant.id
                FROM ChatParticipant participant
                WHERE participant.chatRoom = cr
                AND participant.memberId = :memberId
            )
            """)
    Optional<ChatRoom> findByIdAndMemberIdWithParticipants(
            @Param("roomId") Long roomId,
            @Param("memberId") Long memberId
    );

    @Query("""
            SELECT COUNT(participant) > 0
            FROM ChatParticipant participant
            JOIN ChatParticipant otherParticipant
            ON participant.chatRoom = otherParticipant.chatRoom
            WHERE participant.memberId = :currentMemberId
            AND otherParticipant.memberId = :otherMemberId
            AND participant.isActive = true
            """)
    boolean existsActiveChatRoom(
            @Param("currentMemberId") Long currentMemberId,
            @Param("otherMemberId") Long otherMemberId
    );
}
