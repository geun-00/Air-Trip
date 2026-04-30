package project.chat.application.out.command;

import project.chat.domain.ChatRoom;

public interface SaveChatRoomPort {

    ChatRoom save(ChatRoom chatRoom);
}
