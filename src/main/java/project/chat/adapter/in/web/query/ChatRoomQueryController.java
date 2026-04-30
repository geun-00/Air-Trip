package project.chat.adapter.in.web.query;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.chat.adapter.in.web.response.ChatMessagesResponse;
import project.chat.adapter.in.web.response.ChatRoomResponse;
import project.chat.adapter.in.websocket.response.ChatMessageResponse;
import project.chat.application.in.query.GetChatMessagesUseCase;
import project.chat.application.in.query.GetChatRoomsUseCase;
import project.chat.application.in.query.model.ChatMessageView;
import project.chat.application.in.query.model.ChatMessagesView;
import project.chat.application.in.query.model.ChatRoomView;
import project.chat.application.in.query.model.GetChatMessagesQuery;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomQueryController {

    private final GetChatRoomsUseCase getChatRoomsUseCase;
    private final GetChatMessagesUseCase getChatMessagesUseCase;

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(@CurrentMemberId Long memberId) {
        List<ChatRoomResponse> response = getChatRoomsUseCase.getChatRooms(memberId)
                                                             .stream()
                                                             .map(this::toResponse)
                                                             .toList();
        return ResponseEntity.ok(response);
    }

    private ChatRoomResponse toResponse(ChatRoomView view) {
        return new ChatRoomResponse(
                view.roomId(),
                view.customRoomName(),
                view.memberId(),
                view.memberName(),
                view.memberProfileImage(),
                view.otherMemberActive(),
                view.lastMessage(),
                view.lastMessageTime(),
                view.unreadCount()
        );
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ChatMessagesResponse> getMessageHistories(
            @RequestParam(value = "lastMessageId", required = false) Long lastMessageId,
            @RequestParam("size") int pageSize,
            @PathVariable Long roomId
    ) {
        ChatMessagesView response = getChatMessagesUseCase.getChatMessages(new GetChatMessagesQuery(lastMessageId, roomId, pageSize));

        return ResponseEntity.ok(toResponse(response));
    }

    private ChatMessagesResponse toResponse(ChatMessagesView view) {
        return new ChatMessagesResponse(
                view.messages()
                    .stream()
                    .map(this::toResponse)
                    .toList(),
                view.hasMore()
        );
    }

    private ChatMessageResponse toResponse(ChatMessageView view) {
        return new ChatMessageResponse(
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
