package project.chat.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.chat.domain.exception.ChatExceptions;
import project.common.adapter.out.persistence.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_rooms")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    public static ChatRoom create() {
        return new ChatRoom();
    }

    public void addCreator(Long memberId, String roomName) {
        addParticipant(memberId, true, roomName);
    }

    public void addParticipant(Long memberId, String roomName) {
        addParticipant(memberId, false, roomName);
    }

    private void addParticipant(Long memberId, boolean creator, String roomName) {
        if (hasParticipant(memberId)) {
            return;
        }

        participants.add(ChatParticipant.create(this, memberId, creator, roomName));
    }

    public void leave(Long memberId) {
        findParticipant(memberId).leave();
    }

    public void rejoin(Long memberId) {
        findParticipant(memberId).rejoin();
    }

    public void updateRoomName(Long memberId, String roomName) {
        findParticipant(memberId).updateRoomName(roomName);
    }

    public void updateLastReadMessage(Long memberId, Long messageId) {
        findParticipant(memberId).updateLastReadMessage(messageId);
    }

    public boolean hasParticipant(Long memberId) {
        return participants.stream()
                           .anyMatch(participant -> participant.isMember(memberId));
    }

    public List<Long> getParticipantIds() {
        return participants.stream()
                           .map(ChatParticipant::getMemberId)
                           .toList();
    }

    private ChatParticipant findParticipant(Long memberId) {
        return participants.stream()
                           .filter(participant -> participant.isMember(memberId))
                           .findFirst()
                           .orElseThrow(() -> ChatExceptions.notFoundChatParticipant(id, memberId));
    }
}
