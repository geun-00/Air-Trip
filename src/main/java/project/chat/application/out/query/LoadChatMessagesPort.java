package project.chat.application.out.query;

import project.chat.application.out.query.model.ChatMessageHistoryView;

import java.util.List;

public interface LoadChatMessagesPort {

    List<ChatMessageHistoryView> loadMessages(Long lastMessageId, Long roomId, int pageSize);

    List<ChatMessageHistoryView> loadCachedMessages(Long roomId);
}
