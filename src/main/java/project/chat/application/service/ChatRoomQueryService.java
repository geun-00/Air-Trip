package project.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.chat.application.in.query.GetChatMessagesUseCase;
import project.chat.application.in.query.GetChatRoomsUseCase;
import project.chat.application.in.query.model.ChatMessageView;
import project.chat.application.in.query.model.ChatMessagesView;
import project.chat.application.in.query.model.ChatRoomView;
import project.chat.application.in.query.model.GetChatMessagesQuery;
import project.chat.application.out.command.ChatRoomStatePort;
import project.chat.application.out.command.LoadChatRoomPort;
import project.chat.application.out.query.LoadChatMessagesPort;
import project.chat.application.out.query.model.ChatMessageHistoryView;
import project.chat.application.out.query.model.ChatRoomInfoView;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomQueryService implements GetChatRoomsUseCase, GetChatMessagesUseCase {

    private final LoadChatRoomPort loadChatRoomPort;
    private final ChatRoomStatePort chatRoomStatePort;
    private final LoadChatMessagesPort loadChatMessagesPort;

    @Override
    public List<ChatRoomView> getChatRooms(Long memberId) {
        return loadChatRoomPort.loadChatRooms(memberId)
                               .stream()
                               .map(room -> toView(
                                       room,
                                       chatRoomStatePort.loadUnreadCount(room.roomId(), memberId)
                               ))
                               .toList();
    }

    private ChatRoomView toView(ChatRoomInfoView view, int unreadCount) {
        return new ChatRoomView(
                view.roomId(),
                view.customRoomName(),
                view.memberId(),
                view.memberName(),
                view.memberProfileImage(),
                view.otherMemberActive(),
                view.lastMessage(),
                view.lastMessageTime(),
                unreadCount
        );
    }

    @Override
    public ChatMessagesView getChatMessages(GetChatMessagesQuery query) {
        List<ChatMessageHistoryView> fetchedMessages = loadChatMessagesPort.loadMessages(
                query.lastMessageId(),
                query.roomId(),
                query.pageSize()
        );
        List<ChatMessageHistoryView> resultMessages = new ArrayList<>(fetchedMessages);

        if (query.lastMessageId() == null) {
            List<ChatMessageHistoryView> cachedMessages = loadChatMessagesPort.loadCachedMessages(query.roomId())
                                                                              .stream()
                                                                              .filter(message ->
                                                                                              fetchedMessages.isEmpty() ||
                                                                                                      message.timestamp().isAfter(fetchedMessages.getFirst().timestamp()))
                                                                              .toList();
            resultMessages.addAll(0, cachedMessages);
        }

        boolean hasMore = resultMessages.size() > query.pageSize();
        if (hasMore) {
            resultMessages.removeLast();
        }

        return new ChatMessagesView(
                resultMessages.stream().map(this::toView).toList(),
                hasMore
        );
    }

    private ChatMessageView toView(ChatMessageHistoryView view) {
        return new ChatMessageView(
                view.messageId(),
                view.roomId(),
                view.senderId(),
                view.senderName(),
                view.content(),
                view.timestamp(),
                view.left()
        );
    }
}
