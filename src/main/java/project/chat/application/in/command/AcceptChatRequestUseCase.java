package project.chat.application.in.command;

import project.chat.application.in.command.model.AcceptChatRequestCommand;
import project.chat.application.in.command.model.AcceptChatRequestResult;

public interface AcceptChatRequestUseCase {

    AcceptChatRequestResult acceptChatRequest(AcceptChatRequestCommand command);
}
