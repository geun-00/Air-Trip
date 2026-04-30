package project.chat.application.in.command;

import project.chat.application.in.command.model.RejectChatRequestCommand;

public interface RejectChatRequestUseCase {

    void rejectChatRequest(RejectChatRequestCommand command);
}
