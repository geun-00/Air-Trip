package project.chat.application.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.chat.application.in.command.SendChatMessageUseCase;
import project.chat.application.in.command.model.SendChatMessageCommand;
import project.chat.application.out.command.ChatMessageDeliveryPort;
import project.chat.application.out.command.ChatRoomStatePort;
import project.chat.application.out.command.model.ChatMessagePayload;
import project.chat.application.service.ChatRoomParticipationChecker;
import project.chat.domain.exception.ChatExceptions;
import project.member.application.out.command.ReadMemberPort;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageCommandService implements SendChatMessageUseCase {

    private final ReadMemberPort readMemberPort;
    private final ChatRoomStatePort chatRoomStatePort;
    private final ChatMessageDeliveryPort chatMessageDeliveryPort;
    private final ChatRoomParticipationChecker chatRoomParticipationChecker;

    @Override
    public void sendMessage(SendChatMessageCommand command) {
        validateMessageDelivery(command.roomId(), command.senderId());

        String senderName = readMemberPort.getNameById(command.senderId());

        ChatMessagePayload message = new ChatMessagePayload(
                UUID.randomUUID().toString(),
                command.roomId(),
                command.senderId(),
                senderName,
                command.content(),
                LocalDateTime.now(),
                false
        );

        loadOpponentId(command.roomId(), command.senderId())
                .ifPresent(opponentId -> chatRoomStatePort.incrementUnreadCount(command.roomId(), opponentId));

        chatMessageDeliveryPort.deliver(message);
    }

    private void validateMessageDelivery(Long roomId, Long senderId) {
        if (!chatRoomParticipationChecker.isParticipant(roomId, senderId)) {
            throw ChatExceptions.notFoundChatParticipant(roomId, senderId);
        }
        if (chatRoomStatePort.loadRoomMemberIds(roomId).size() < 2) {
            throw ChatExceptions.participantLeft(roomId, senderId);
        }
    }

    private Optional<Long> loadOpponentId(Long roomId, Long senderId) {
        return chatRoomStatePort.loadRoomMemberIds(roomId)
                                .stream()
                                .filter(memberId -> !memberId.equals(senderId))
                                .findFirst();
    }
}
