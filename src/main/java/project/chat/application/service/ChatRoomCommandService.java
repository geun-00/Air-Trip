package project.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.chat.application.event.ChatLeaveEvent;
import project.chat.application.in.command.LeaveChatRoomUseCase;
import project.chat.application.in.command.MarkChatRoomAsReadUseCase;
import project.chat.application.in.command.UpdateChatRoomNameUseCase;
import project.chat.application.in.command.model.LeaveChatRoomCommand;
import project.chat.application.in.command.model.MarkChatRoomAsReadCommand;
import project.chat.application.in.command.model.UpdateChatRoomNameCommand;
import project.chat.application.in.command.model.UpdateChatRoomNameResult;
import project.chat.application.out.command.LoadChatRoomPort;
import project.chat.application.out.query.model.ChatRoomInfoView;
import project.chat.domain.ChatRoom;
import project.member.application.out.command.LoadMemberPort;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomCommandService implements UpdateChatRoomNameUseCase, LeaveChatRoomUseCase, MarkChatRoomAsReadUseCase {

    private final LoadMemberPort loadMemberPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public UpdateChatRoomNameResult updateChatRoomName(UpdateChatRoomNameCommand command) {
        ChatRoom chatRoom = loadChatRoomPort.loadParticipantChatRoom(command.roomId(), command.memberId());
        chatRoom.updateRoomName(command.memberId(), command.roomName());

        ChatRoomInfoView chatRoomInfo = loadChatRoomPort.loadChatRoomInfo(
                command.memberId(),
                command.otherMemberId(),
                command.roomId()
        );
        int unreadCount = loadChatRoomPort.loadUnreadCount(command.roomId(), command.memberId());

        return new UpdateChatRoomNameResult(
                chatRoomInfo.roomId(),
                chatRoomInfo.customRoomName(),
                chatRoomInfo.memberId(),
                chatRoomInfo.memberName(),
                chatRoomInfo.memberProfileImage(),
                chatRoomInfo.otherMemberActive(),
                chatRoomInfo.lastMessage(),
                chatRoomInfo.lastMessageTime(),
                unreadCount
        );
    }

    @Override
    public void leaveChatRoom(LeaveChatRoomCommand command) {
        ChatRoom chatRoom = loadChatRoomPort.loadParticipantChatRoom(command.roomId(), command.memberId());
        chatRoom.leave(command.memberId());

        loadChatRoomPort.markLatestMessageAsRead(command.roomId(), chatRoom, command.memberId());
        loadChatRoomPort.removeRoomMember(command.roomId(), command.memberId());

        String memberName = loadMemberPort.loadMemberName(command.memberId());

        eventPublisher.publishEvent(new ChatLeaveEvent(memberName, command.roomId()));
    }

    @Override
    public void markAsRead(MarkChatRoomAsReadCommand command) {
        loadChatRoomPort.resetUnreadCount(command.roomId(), command.memberId());

        ChatRoom chatRoom = loadChatRoomPort.loadParticipantChatRoom(command.roomId(), command.memberId());
        loadChatRoomPort.markLatestMessageAsRead(command.roomId(), chatRoom, command.memberId());
    }
}
