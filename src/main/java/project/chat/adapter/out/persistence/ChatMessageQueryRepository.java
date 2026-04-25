package project.chat.adapter.out.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.stereotype.Repository;
import project.chat.adapter.in.websocket.response.ChatMessageResponse;
import project.chat.domain.ChatMessage;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;

import java.util.List;

import static project.chat.domain.QChatMessage.chatMessage;
import static project.chat.domain.QChatParticipant.chatParticipant;
import static project.chat.domain.QChatRoom.chatRoom;
import static project.member.domain.QMember.member;

@Repository
public class ChatMessageQueryRepository extends CustomQuerydslRepositorySupport {

    private static final StringPath MEMBER_NAME = Expressions.stringPath(member, "name");

    public ChatMessageQueryRepository() {
        super(ChatMessage.class);
    }

    public List<ChatMessageResponse> getMessages(Long lastMessageId, Long roomId, int pageSize) {

        return select(Projections.constructor(
                ChatMessageResponse.class,
                chatMessage.id,
                chatRoom.id,
                member.id,
                MEMBER_NAME,
                chatMessage.content,
                chatMessage.createdAt))
                .from(chatMessage)
                .join(chatMessage.chatRoom, chatRoom)
                .join(chatMessage.writer, member)
                .join(chatParticipant).on(chatParticipant.chatRoom.eq(chatRoom).and(chatParticipant.member.eq(member)))
                .where(chatRoom.id.eq(roomId),
                        lastMessageId != null ? chatMessage.id.lt(lastMessageId) : null,
                        chatParticipant.lastRejoinedAt.isNull()
                                                      .or(chatMessage.createdAt.after(chatParticipant.lastRejoinedAt))
                )
                .orderBy(chatMessage.id.desc())
                .limit(pageSize + 1)
                .fetch();
    }

}
