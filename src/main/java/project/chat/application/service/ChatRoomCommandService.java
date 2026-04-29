package project.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.chat.application.in.command.UpdateChatRoomNameUseCase;
import project.chat.application.in.command.model.UpdateChatRoomNameCommand;
import project.chat.application.in.command.model.UpdateChatRoomNameResult;
import project.chat.application.out.command.LoadChatRoomPort;
import project.chat.application.out.query.model.ChatRoomInfoView;
import project.chat.domain.ChatRoom;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomCommandService implements UpdateChatRoomNameUseCase {

    private final LoadChatRoomPort loadChatRoomPort;

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
}
