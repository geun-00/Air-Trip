package project.chat.application.in.command;

import project.chat.application.in.command.model.MarkChatRoomAsReadCommand;

public interface MarkChatRoomAsReadUseCase {

    void markAsRead(MarkChatRoomAsReadCommand command);
}
