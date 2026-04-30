package project.chat.application.in.command;

import project.chat.application.in.command.model.SendChatMessageCommand;

public interface SendChatMessageUseCase {

    void sendMessage(SendChatMessageCommand command);
}
