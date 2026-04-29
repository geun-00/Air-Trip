package project.chat.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.chat.adapter.in.web.request.UpdateChatRoomNameRequest;
import project.chat.adapter.in.web.response.ChatRoomResponse;
import project.chat.application.in.command.LeaveChatRoomUseCase;
import project.chat.application.in.command.UpdateChatRoomNameUseCase;
import project.chat.application.in.command.model.LeaveChatRoomCommand;
import project.chat.application.in.command.model.UpdateChatRoomNameCommand;
import project.chat.application.in.command.model.UpdateChatRoomNameResult;
import project.chat.application.service.ChatRoomService;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomCommandController {

    private final ChatRoomService chatRoomService;
    private final LeaveChatRoomUseCase leaveChatRoomUseCase;
    private final UpdateChatRoomNameUseCase updateChatRoomNameUseCase;

    @PatchMapping("/{roomId}/name")
    public ResponseEntity<ChatRoomResponse> updateChatRoomName(
            @Valid @RequestBody UpdateChatRoomNameRequest request,
            @PathVariable Long roomId,
            @CurrentMemberId Long memberId
    ) {
        UpdateChatRoomNameResult result = updateChatRoomNameUseCase.updateChatRoomName(
                new UpdateChatRoomNameCommand(
                        roomId,
                        memberId,
                        request.otherMemberId(),
                        request.customName()
                )
        );

        return ResponseEntity.ok(toResponse(result));
    }

    private ChatRoomResponse toResponse(UpdateChatRoomNameResult result) {
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

    @PostMapping("/{roomId}")
    public ResponseEntity<Void> leaveChatRoom(
            @PathVariable Long roomId,
            @CurrentMemberId Long memberId
    ) {
        leaveChatRoomUseCase.leaveChatRoom(new LeaveChatRoomCommand(roomId, memberId));

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{roomId}/read")
    public ResponseEntity<Void> markChatRoomAsRead(
            @PathVariable Long roomId,
            @CurrentMemberId Long memberId
    ) {
        chatRoomService.markChatRoomAsRead(roomId, memberId);
        return ResponseEntity.ok().build();
    }
}
