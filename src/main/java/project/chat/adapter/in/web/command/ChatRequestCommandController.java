package project.chat.adapter.in.web.command;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.chat.adapter.in.web.request.RequestChatRequest;
import project.chat.adapter.in.web.response.ChatRoomResponse;
import project.chat.adapter.in.web.response.RequestChatResponse;
import project.chat.application.in.command.AcceptChatRequestUseCase;
import project.chat.application.in.command.RejectChatRequestUseCase;
import project.chat.application.in.command.RequestChatUseCase;
import project.chat.application.in.command.model.AcceptChatRequestCommand;
import project.chat.application.in.command.model.AcceptChatRequestResult;
import project.chat.application.in.command.model.RejectChatRequestCommand;
import project.chat.application.in.command.model.RequestChatCommand;
import project.chat.application.in.command.model.ChatRequest;

@RestController
@RequestMapping("/api/chat/requests")
@RequiredArgsConstructor
public class ChatRequestCommandController {

    private final RequestChatUseCase requestChatUseCase;
    private final AcceptChatRequestUseCase acceptChatRequestUseCase;
    private final RejectChatRequestUseCase rejectChatRequestUseCase;

    @PostMapping
    public ResponseEntity<RequestChatResponse> requestChat(
            @Valid @RequestBody RequestChatRequest request,
            @CurrentMemberId Long senderId
    ) {
        ChatRequest result = requestChatUseCase.requestChat(new RequestChatCommand(senderId, request.receiverId()));

        return ResponseEntity.ok(toResponse(result));
    }

    private RequestChatResponse toResponse(ChatRequest result) {
        return new RequestChatResponse(
                result.requestId(),
                result.senderId(),
                result.senderName(),
                result.senderProfileImage(),
                result.receiverId(),
                result.receiverName(),
                result.receiverProfileImage(),
                result.expiresAt()
        );
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<ChatRoomResponse> acceptRequestChat(
            @PathVariable String requestId,
            @CurrentMemberId Long memberId
    ) {
        AcceptChatRequestResult result = acceptChatRequestUseCase.acceptChatRequest(new AcceptChatRequestCommand(requestId, memberId));

        return ResponseEntity.ok(toResponse(result));
    }

    private ChatRoomResponse toResponse(AcceptChatRequestResult result) {
        return new ChatRoomResponse(
                result.roomId(),
                result.customRoomName(),
                result.memberId(),
                result.memberName(),
                result.memberProfileImage(),
                result.otherMemberActive(),
                result.lastMessage(),
                result.lastMessageTime(),
                result.unreadCount()
        );
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectRequestChat(
            @PathVariable String requestId,
            @CurrentMemberId Long memberId
    ) {
        rejectChatRequestUseCase.rejectChatRequest(new RejectChatRequestCommand(requestId, memberId));

        return ResponseEntity.ok().build();
    }
}
