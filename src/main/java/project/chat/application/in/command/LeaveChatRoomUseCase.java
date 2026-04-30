package project.chat.application.in.command;

import project.chat.application.in.command.model.LeaveChatRoomCommand;

public interface LeaveChatRoomUseCase {

    void leaveChatRoom(LeaveChatRoomCommand command);
}
