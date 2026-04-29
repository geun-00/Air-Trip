package project.chat.adapter.out.persistence;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.stereotype.Repository;
import project.chat.adapter.out.persistence.model.ChatMessageHistoryRow;
import project.chat.domain.ChatMessage;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static project.chat.domain.QChatMessage.chatMessage;
import static project.chat.domain.QChatParticipant.chatParticipant;
import static project.member.domain.QMember.member;

@Repository
public class ChatMessageQueryRepository extends CustomQuerydslRepositorySupport {

    private static final StringPath MEMBER_NAME = Expressions.stringPath(member, "name");

    public ChatMessageQueryRepository() {
        super(ChatMessage.class);
    }

    public List<ChatMessageHistoryRow> getMessages(
            Long lastMessageId,
            Long roomId,
            int pageSize
    ) {
        return select(constructor(ChatMessageHistoryRow.class,
                chatMessage.id,
                chatMessage.chatRoomId,
                member.id,
                MEMBER_NAME,
                chatMessage.content,
                chatMessage.createdAt))
                .from(chatMessage)
                .join(member).on(member.id.eq(chatMessage.writerId))
                .join(chatParticipant).on(
                        chatParticipant.chatRoom.id.eq(chatMessage.chatRoomId),
                        chatParticipant.memberId.eq(chatMessage.writerId)
                )
                .where(
                        chatMessage.chatRoomId.eq(roomId),
                        lastMessageId != null ? chatMessage.id.lt(lastMessageId) : null,
                        chatParticipant.lastRejoinedAt.isNull().or(chatMessage.createdAt.after(chatParticipant.lastRejoinedAt))
                )
                .orderBy(chatMessage.id.desc())
                .limit(pageSize + 1)
                .fetch();
    }

}
