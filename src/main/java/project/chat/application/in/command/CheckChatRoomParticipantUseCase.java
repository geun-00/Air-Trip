package project.chat.application.in.command;

public interface CheckChatRoomParticipantUseCase {

    boolean isChatRoomParticipant(Long roomId, Long memberId);
}
