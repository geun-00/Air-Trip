package project.chat.application.in.query;

import project.chat.application.in.query.model.ChatMessagesView;
import project.chat.application.in.query.model.GetChatMessagesQuery;

public interface GetChatMessagesUseCase {

    ChatMessagesView getChatMessages(GetChatMessagesQuery query);
}
