package project.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import project.common.adapter.out.persistence.BaseEntity;

@Entity
@Getter
@Table(name = "chat_rooms")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id", nullable = false)
    private Long id;
}
