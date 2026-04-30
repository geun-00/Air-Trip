package project.chat.adapter.in.web.query;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.chat.adapter.in.web.response.RequestChatResponse;
import project.chat.application.in.query.GetReceivedChatRequestsUseCase;
import project.chat.application.in.query.GetSentChatRequestsUseCase;
import project.chat.application.in.query.model.ChatRequestView;

import java.util.List;

@RestController
@RequestMapping("/api/chat/requests")
@RequiredArgsConstructor
public class ChatRequestQueryController {

    private final GetSentChatRequestsUseCase getSentChatRequestsUseCase;
    private final GetReceivedChatRequestsUseCase getReceivedChatRequestsUseCase;

    @GetMapping("/received")
    public ResponseEntity<List<RequestChatResponse>> getReceivedChatRequests(@CurrentMemberId Long memberId) {
        List<RequestChatResponse> response = getReceivedChatRequestsUseCase.getReceivedChatRequests(memberId)
                                                                           .stream()
                                                                           .map(this::toResponse)
                                                                           .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sent")
    public ResponseEntity<List<RequestChatResponse>> getSentChatRequests(@CurrentMemberId Long memberId) {
        List<RequestChatResponse> response = getSentChatRequestsUseCase.getSentChatRequests(memberId)
                                                                       .stream()
                                                                       .map(this::toResponse)
                                                                       .toList();
        return ResponseEntity.ok(response);
    }

    private RequestChatResponse toResponse(ChatRequestView view) {
        return new RequestChatResponse(
                view.requestId(),
                view.senderId(),
                view.senderName(),
                view.senderProfileImage(),
                view.receiverId(),
                view.receiverName(),
                view.receiverProfileImage(),
                view.expiresAt()
        );
    }
}
