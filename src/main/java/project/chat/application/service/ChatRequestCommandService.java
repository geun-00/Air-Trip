package project.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.chat.application.event.ChatRequestAcceptedEvent;
import project.chat.application.event.ChatRequestCreatedEvent;
import project.chat.application.event.ChatRequestRejectedEvent;
import project.chat.application.in.command.AcceptChatRequestUseCase;
import project.chat.application.in.command.RejectChatRequestUseCase;
import project.chat.application.in.command.RequestChatUseCase;
import project.chat.application.in.command.model.AcceptChatRequestCommand;
import project.chat.application.in.command.model.AcceptChatRequestResult;
import project.chat.application.in.command.model.RejectChatRequestCommand;
import project.chat.application.in.command.model.RequestChatCommand;
import project.chat.application.in.command.model.ChatRequest;
import project.chat.application.out.command.ChatRequestPort;
import project.chat.application.out.command.ChatRoomStatePort;
import project.chat.application.out.command.LoadChatRoomPort;
import project.chat.application.out.command.SaveChatRoomPort;
import project.chat.application.out.command.model.SaveChatRequestCommand;
import project.chat.application.out.query.model.ChatRoomInfoView;
import project.chat.domain.ChatRoom;
import project.chat.domain.exception.ChatExceptions;
import project.member.application.out.command.LoadMemberPort;
import project.member.domain.Member;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatRequestCommandService implements RequestChatUseCase, AcceptChatRequestUseCase, RejectChatRequestUseCase {

    private final LoadMemberPort loadMemberPort;
    private final ChatRequestPort chatRequestPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final SaveChatRoomPort saveChatRoomPort;
    private final ChatRoomStatePort chatRoomStatePort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ChatRequest requestChat(RequestChatCommand command) {
        validateRequestable(command.senderId(), command.receiverId());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plus(Duration.ofDays(1));

        Member sender = loadMemberPort.loadById(command.senderId());
        Member receiver = loadMemberPort.loadById(command.receiverId());

        ChatRequest result = chatRequestPort.save(new SaveChatRequestCommand(
                sender.getId(),
                sender.getName(),
                sender.getProfileUrl(),
                receiver.getId(),
                receiver.getName(),
                receiver.getProfileUrl(),
                now,
                expiresAt
        ));

        eventPublisher.publishEvent(new ChatRequestCreatedEvent(result));
        return result;
    }

    private void validateRequestable(Long senderId, Long receiverId) {
        if (receiverId.equals(senderId)) {
            throw ChatExceptions.sameParticipant(receiverId);
        }
        if (chatRequestPort.existsBySenderIdAndReceiverId(senderId, receiverId)) {
            throw ChatExceptions.alreadyRequest("senderId=%d, receiverId=%d".formatted(senderId, receiverId));
        }
        if (loadChatRoomPort.existsActiveChatRoom(senderId, receiverId)) {
            throw ChatExceptions.alreadyActiveChat();
        }
    }

    @Override
    @Transactional
    public AcceptChatRequestResult acceptChatRequest(AcceptChatRequestCommand command) {
        ChatRequest chatRequest = chatRequestPort.load(command.requestId());
        validateAcceptable(command, chatRequest);

        chatRequestPort.delete(command.requestId());

        Long senderId = chatRequest.senderId();
        ChatRoom chatRoom = getOrCreateChatRoom(chatRequest, command.receiverId());
        chatRoomStatePort.addRoomMembers(chatRoom.getId(), senderId, command.receiverId());

        AcceptChatRequestResult senderChatRoom = getChatRoomResult(senderId, command.receiverId(), chatRoom.getId());
        eventPublisher.publishEvent(new ChatRequestAcceptedEvent(command.requestId(), senderId, senderChatRoom));

        return getChatRoomResult(command.receiverId(), senderId, chatRoom.getId());
    }

    private void validateAcceptable(AcceptChatRequestCommand command, ChatRequest chatRequest) {
        if (!chatRequest.receiverId().equals(command.receiverId())) {
            throw ChatExceptions.notOwnerOfChatRequest(command.requestId(), command.receiverId());
        }
    }

    private ChatRoom getOrCreateChatRoom(ChatRequest chatRequest, Long receiverId) {
        return loadChatRoomPort.findChatRoomByMembersId(receiverId, chatRequest.senderId())
                               .map(chatRoom -> {
                                   chatRoom.rejoin(receiverId);
                                   chatRoom.rejoin(chatRequest.senderId());
                                   return chatRoom;
                               })
                               .orElseGet(() -> createChatRoom(chatRequest));
    }

    private ChatRoom createChatRoom(ChatRequest chatRequest) {
        ChatRoom chatRoom = ChatRoom.create();
        chatRoom.addParticipant(chatRequest.receiverId(), chatRequest.senderName() + "님과의 대화");
        chatRoom.addCreator(chatRequest.senderId(), chatRequest.receiverName() + "님과의 대화");

        return saveChatRoomPort.save(chatRoom);
    }

    private AcceptChatRequestResult getChatRoomResult(Long memberId, Long otherMemberId, Long roomId) {
        ChatRoomInfoView chatRoomInfo = loadChatRoomPort.loadChatRoomInfo(memberId, otherMemberId, roomId);
        int unreadCount = chatRoomStatePort.loadUnreadCount(roomId, memberId);

        return new AcceptChatRequestResult(
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
    public void rejectChatRequest(RejectChatRequestCommand command) {
        ChatRequest chatRequest = chatRequestPort.load(command.requestId());
        validateRejectable(command, chatRequest);

        chatRequestPort.delete(command.requestId());
        eventPublisher.publishEvent(new ChatRequestRejectedEvent(
                command.requestId(),
                chatRequest.senderId(),
                chatRequest.receiverName()
        ));
    }

    private void validateRejectable(RejectChatRequestCommand command, ChatRequest chatRequest) {
        if (!chatRequest.receiverId().equals(command.rejecterId())) {
            throw ChatExceptions.notOwnerOfChatRequest(command.requestId(), command.rejecterId());
        }
    }
}
