package project.chat.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.chat.adapter.in.web.response.ChatMessagesResponse;
import project.chat.adapter.in.web.response.ChatRoomResponse;
import project.chat.adapter.in.web.request.LeaveChatRoomRequest;
import project.chat.adapter.in.web.request.UpdateChatRoomNameRequest;
import project.chat.application.service.ChatRoomService;
import project.chat.application.service.ChatMessageService;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(@CurrentMemberId Long memberId) {
        List<ChatRoomResponse> response = chatRoomService.getChatRooms(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ChatMessagesResponse> getMessageHistories(@RequestParam(value = "lastMessageId", required = false) Long lastMessageId,
                                                                    @RequestParam("size") int pageSize,
                                                                    @PathVariable("roomId") Long roomId) {
        ChatMessagesResponse response = chatMessageService.getMessageHistories(lastMessageId, roomId, pageSize);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{roomId}/name")
    public ResponseEntity<?> updateChatRoomName(@Valid @RequestBody UpdateChatRoomNameRequest reqDto,
                                                @PathVariable("roomId") Long roomId,
                                                @CurrentMemberId Long memberId) {
        ChatRoomResponse response = chatRoomService.updateChatRoomName(reqDto.customName(), reqDto.otherMemberId(), memberId, roomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<?> leaveChatRoom(@PathVariable("roomId") Long roomId,
                                           @RequestBody LeaveChatRoomRequest reqDto,
                                           @CurrentMemberId Long memberId) {
        chatRoomService.leaveChatRoom(roomId, memberId, reqDto.isActive());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{roomId}/read")
    public ResponseEntity<?> markChatRoomAsRead(@PathVariable("roomId") Long roomId,
                                                @CurrentMemberId Long memberId) {
        chatRoomService.markChatRoomAsRead(roomId, memberId);
        return ResponseEntity.ok().build();
    }
}
