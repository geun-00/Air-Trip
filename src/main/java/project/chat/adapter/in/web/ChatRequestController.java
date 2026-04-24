package project.chat.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.chat.adapter.in.web.response.ChatRoomResponse;
import project.chat.adapter.in.web.request.RequestChatRequest;
import project.chat.adapter.in.web.response.RequestChatResponse;
import project.chat.application.service.ChatRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/chat/requests")
@RequiredArgsConstructor
public class ChatRequestController {

    private final ChatRequestService chatRequestService;

    @PostMapping
    public ResponseEntity<RequestChatResponse> requestChat(@Valid @RequestBody RequestChatRequest requestChatRequest,
                                                           @CurrentMemberId Long senderId) {
        RequestChatResponse response = chatRequestService.requestChat(requestChatRequest.receiverId(), senderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<ChatRoomResponse> acceptRequestChat(@PathVariable("requestId") String requestId,
                                                              @CurrentMemberId Long memberId) {
        ChatRoomResponse response = chatRequestService.acceptRequestChat(requestId, memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectRequestChat(@PathVariable("requestId") String requestId,
                                               @CurrentMemberId Long memberId) {
        chatRequestService.rejectRequestChat(requestId, memberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/received")
    public ResponseEntity<List<RequestChatResponse>> getReceivedChatRequests(@CurrentMemberId Long memberId) {
        List<RequestChatResponse> response = chatRequestService.getReceivedChatRequests(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sent")
    public ResponseEntity<List<RequestChatResponse>> getSentChatRequests(@CurrentMemberId Long memberId) {
        List<RequestChatResponse> response = chatRequestService.getSentChatRequests(memberId);
        return ResponseEntity.ok(response);
    }
}
