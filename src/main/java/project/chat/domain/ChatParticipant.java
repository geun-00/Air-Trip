package project.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_participants",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"chat_room_id", "member_id"})
        })
class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "last_read_message")
    private Long lastReadMessageId;

    @Column(name = "is_creator", nullable = false)
    private Boolean isCreator;

    @Column(name = "custom_room_name", nullable = false)
    private ChatRoomName customRoomName;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "last_rejoined_at")
    private LocalDateTime lastRejoinedAt;

    static ChatParticipant create(
            ChatRoom chatRoom,
            Long memberId,
            Boolean isCreator,
            String roomName
    ) {
        return new ChatParticipant(chatRoom, memberId, isCreator, new ChatRoomName(roomName));
    }

    private ChatParticipant(
            ChatRoom chatRoom,
            Long memberId,
            Boolean isCreator,
            ChatRoomName customRoomName
    ) {
        this.chatRoom = chatRoom;
        this.memberId = memberId;
        this.isCreator = isCreator;
        this.customRoomName = customRoomName;
    }

    void leave() {
        if (!this.isActive) {
            return;
        }
        this.isActive = false;
        this.leftAt = LocalDateTime.now();
    }

    void rejoin() {
        if (this.isActive) {
            return;
        }
        this.isActive = true;
        this.lastRejoinedAt = LocalDateTime.now();
    }

    void updateRoomName(String roomName) {
        this.customRoomName = new ChatRoomName(roomName);
    }

    void updateLastReadMessage(Long messageId) {
        this.lastReadMessageId = messageId;
    }

    boolean isMember(Long memberId) {
        return this.memberId.equals(memberId);
    }
}
