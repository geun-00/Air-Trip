package project.chat.application.in.query;

import project.chat.application.in.query.model.ChatRoomView;

import java.util.List;

public interface GetChatRoomsUseCase {

    List<ChatRoomView> getChatRooms(Long memberId);
}
