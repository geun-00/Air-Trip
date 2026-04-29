package project.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.adapter.out.persistence.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_messages")
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "member_id", nullable = false)
    private Long writerId;

    @Column(name = "content", nullable = false)
    private ChatMessageContent content;

    public static ChatMessage create(Long chatRoomId, Long writerId, String content) {
        return new ChatMessage(chatRoomId, writerId, new ChatMessageContent(content));
    }

    private ChatMessage(Long chatRoomId, Long writerId, ChatMessageContent content) {
        this.chatRoomId = chatRoomId;
        this.writerId = writerId;
        this.content = content;
    }

    public String getContent() {
        return content.value();
    }
}
