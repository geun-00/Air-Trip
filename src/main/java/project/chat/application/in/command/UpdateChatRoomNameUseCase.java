package project.chat.application.in.command;

import project.chat.application.in.command.model.UpdateChatRoomNameCommand;
import project.chat.application.in.command.model.UpdateChatRoomNameResult;

public interface UpdateChatRoomNameUseCase {

    UpdateChatRoomNameResult updateChatRoomName(UpdateChatRoomNameCommand command);
}
