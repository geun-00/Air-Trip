package project.chatbot.adapter.in.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.chatbot.adapter.in.web.request.AskChatbotRequest;
import project.chatbot.adapter.in.web.response.ChatbotHistoryResponse;
import project.chatbot.adapter.in.web.response.AskChatbotResponse;
import project.chatbot.application.in.command.AskChatbotUseCase;
import project.chatbot.application.in.command.model.AskChatbotCommand;
import project.chatbot.application.in.command.model.AskChatbotResult;
import project.chatbot.application.in.query.GetChatbotMessagesUseCase;
import project.chatbot.application.in.query.model.ChatbotHistoryQuery;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat-bot")
@RequiredArgsConstructor
public class ChatbotController {

    private final AskChatbotUseCase askChatbotUseCase;
    private final GetChatbotMessagesUseCase getChatbotMessagesUseCase;

    @PostMapping
    public ResponseEntity<AskChatbotResponse> postMessage(
            @CurrentMemberId(required = false) Long memberId,
            @RequestBody AskChatbotRequest request,
            HttpSession session
    ) {
        String conversationId = resolveConversationId(memberId, session);
        AskChatbotResult result = askChatbotUseCase.ask(new AskChatbotCommand(memberId, conversationId, request.message()));

        return ResponseEntity.ok(new AskChatbotResponse(result.textResponse(), result.metadata()));
    }

    @GetMapping
    public ResponseEntity<List<ChatbotHistoryResponse>> getMessages(
            @CurrentMemberId(required = false) Long memberId,
            HttpSession session
    ) {
        String conversationId = resolveConversationId(memberId, session);
        List<ChatbotHistoryResponse> response = getChatbotMessagesUseCase.getMessages(new ChatbotHistoryQuery(conversationId))
                                                                         .stream()
                                                                         .map(result -> new ChatbotHistoryResponse(
                                                                                 result.type(),
                                                                                 result.content(),
                                                                                 result.metadata(),
                                                                                 result.createdAt()
                                                                         ))
                                                                         .toList();
        return ResponseEntity.ok(response);
    }

    private String resolveConversationId(Long memberId, HttpSession session) {
        if (memberId != null) {
            return memberId.toString();
        }

        final String sessionKey = "conversationId";
        String conversationId = (String) session.getAttribute(sessionKey);

        if (!StringUtils.hasText(conversationId)) {
            conversationId = UUID.randomUUID().toString();
            session.setAttribute(sessionKey, conversationId);
        }

        return conversationId;
    }
}
