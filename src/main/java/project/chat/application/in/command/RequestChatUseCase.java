package project.chat.application.in.command;

import project.chat.application.in.command.model.RequestChatCommand;
import project.chat.application.in.command.model.ChatRequest;

public interface RequestChatUseCase {

    ChatRequest requestChat(RequestChatCommand command);
}
