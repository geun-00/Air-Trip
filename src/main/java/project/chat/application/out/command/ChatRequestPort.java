package project.chat.application.out.command;

import project.chat.application.in.command.model.ChatRequest;
import project.chat.application.out.command.model.SaveChatRequestCommand;

public interface ChatRequestPort {

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    ChatRequest load(String requestId);

    ChatRequest save(SaveChatRequestCommand request);

    void delete(String requestId);
}
