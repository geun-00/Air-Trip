package project.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.chat.application.in.command.CheckChatRoomParticipantUseCase;

@Service
@RequiredArgsConstructor
public class ChatRoomParticipationService implements CheckChatRoomParticipantUseCase {

    private final ChatRoomParticipationChecker chatRoomParticipationChecker;

    @Override
    public boolean isChatRoomParticipant(Long roomId, Long memberId) {
        return chatRoomParticipationChecker.isParticipant(roomId, memberId);
    }
}
