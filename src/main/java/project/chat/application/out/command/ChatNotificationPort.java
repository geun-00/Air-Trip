package project.chat.application.out.command;

import project.chat.application.in.command.model.AcceptChatRequestResult;
import project.chat.application.in.command.model.ChatRequest;

public interface ChatNotificationPort {

    void sendChatRequest(ChatRequest chatRequest);

    void sendChatRequestAccepted(String requestId, Long senderId, AcceptChatRequestResult chatRoom);

    void sendChatRequestRejected(String requestId, Long senderId, String receiverName);

    void sendChatRoomLeft(String memberName, Long roomId);
}
