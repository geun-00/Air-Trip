package project.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.chat.application.out.command.ChatRoomStatePort;
import project.chat.application.out.command.LoadChatRoomPort;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ChatRoomParticipationChecker {

    private final LoadChatRoomPort loadChatRoomPort;
    private final ChatRoomStatePort chatRoomStatePort;

    public boolean isParticipant(Long roomId, Long memberId) {
        try {
            return checkParticipation(roomId, memberId);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkParticipation(Long roomId, Long memberId) {
        boolean member = chatRoomStatePort.isRoomMember(roomId, memberId);

        if (!member && !chatRoomStatePort.existsRoomMembers(roomId)) {
            refreshChatMembers(roomId);
            member = chatRoomStatePort.isRoomMember(roomId, memberId);
        }

        return member;
    }

    private void refreshChatMembers(Long roomId) {
        Set<Long> participantIds = loadChatRoomPort.loadParticipantIds(roomId);
        if (!participantIds.isEmpty()) {
            chatRoomStatePort.addRoomMembers(roomId, participantIds.toArray(Long[]::new));
        }
    }
}
