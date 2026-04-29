package project.chat.adapter.out.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.stereotype.Repository;
import project.chat.adapter.in.web.response.ChatRoomResponse;
import project.chat.adapter.out.persistence.model.ChatRoomInfoRow;
import project.chat.domain.ChatRoom;
import project.chat.domain.QChatMessage;
import project.chat.domain.QChatParticipant;
import project.common.adapter.out.persistence.CustomQuerydslRepositorySupport;
import project.member.domain.QMember;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.*;
import static project.chat.domain.QChatMessage.chatMessage;
import static project.chat.domain.QChatRoom.chatRoom;

@Repository
public class ChatRoomQueryRepository extends CustomQuerydslRepositorySupport {

    private static final QChatParticipant CP1 = new QChatParticipant("cp1");
    private static final QChatParticipant CP2 = new QChatParticipant("cp2");
    private static final QMember OTHER_MEMBER = new QMember("otherMember");
    private static final QChatMessage LATEST_MESSAGE = new QChatMessage("latestMessage");

    public ChatRoomQueryRepository() {
        super(ChatRoom.class);
    }

    public Optional<ChatRoomInfoRow> findChatRoomInfo(
            Long currentMemberId,
            Long otherMemberId,
            Long roomId
    ) {
        return Optional.ofNullable(
                select(constructor(ChatRoomInfoRow.class,
                        chatRoom.id,
                        CP1.customRoomName,
                        OTHER_MEMBER.id,
                        OTHER_MEMBER.name,
                        OTHER_MEMBER.detail.profileUrl,
                        CP2.isActive,
                        chatMessage.content,
                        chatMessage.createdAt))
                        .from(chatRoom)
                        .join(CP1).on(
                                CP1.chatRoom.eq(chatRoom),
                                CP1.memberId.eq(currentMemberId)
                        )
                        .join(CP2).on(
                                CP2.chatRoom.eq(chatRoom),
                                CP2.memberId.eq(otherMemberId)
                        )
                        .join(OTHER_MEMBER).on(OTHER_MEMBER.id.eq(CP2.memberId))
                        .leftJoin(chatMessage).on(chatMessage.id.eq(latestMessageIdSubQuery()))
                        .where(chatRoom.id.eq(roomId))
                        .fetchOne()
        );
    }

    public List<ChatRoomResponse> findChatRooms(Long memberId) {
        return select(constructor(
                ChatRoomResponse.class,
                chatRoom.id,
                CP1.customRoomName,
                OTHER_MEMBER.id,
                OTHER_MEMBER.name,
                OTHER_MEMBER.detail.profileUrl,
                CP2.isActive,
                chatMessage.content,
                chatMessage.createdAt,
                Expressions.asNumber(0)))
                .from(chatRoom)
                .join(CP1).on(
                        CP1.chatRoom.eq(chatRoom),
                        CP1.memberId.eq(memberId),
                        CP1.isActive.isTrue()
                )
                .join(CP2).on(
                        CP2.chatRoom.eq(chatRoom),
                        CP2.memberId.ne(memberId)
                )
                .join(OTHER_MEMBER).on(OTHER_MEMBER.id.eq(CP2.memberId))
                .leftJoin(chatMessage).on(chatMessage.id.eq(latestMessageIdSubQuery()))
                .orderBy(chatMessage.createdAt.desc().nullsLast())
                .fetch();
    }

    private JPQLQuery<Long> latestMessageIdSubQuery() {
        return JPAExpressions.select(LATEST_MESSAGE.id.max())
                             .from(LATEST_MESSAGE)
                             .where(LATEST_MESSAGE.chatRoomId.eq(chatRoom.id));
    }
}
