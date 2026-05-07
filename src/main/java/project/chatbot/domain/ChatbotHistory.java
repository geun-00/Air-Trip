package project.chatbot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.adapter.out.persistence.BaseEntity;

import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatbot_histories")
public class ChatbotHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatbot_history_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private ChatbotMessageType type;

    @Column(name = "text", nullable = false, columnDefinition = "LONGTEXT")
    private String text;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "metadata", columnDefinition = "LONGTEXT")
    private Map<String, Object> metadata;

    public static ChatbotHistory of(
            String conversationId,
            ChatbotMessageType type,
            String text,
            Map<String, Object> metadata
    ) {
        return new ChatbotHistory(type, text, conversationId, metadata);
    }

    private ChatbotHistory(
            ChatbotMessageType type,
            String text,
            String conversationId,
            Map<String, Object> metadata
    ) {
        this.type = type;
        this.text = text;
        this.conversationId = conversationId;
        this.metadata = metadata;
    }
}
