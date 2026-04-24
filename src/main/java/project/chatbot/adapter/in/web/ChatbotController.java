package project.chatbot.adapter.in.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.chatbot.adapter.in.web.request.ChatbotRequest;
import project.chatbot.adapter.in.web.response.ChatbotHistoryDto;
import project.chatbot.adapter.in.web.response.ChatbotResponseDto;
import project.chatbot.application.service.ChatbotService;

import java.util.List;

@RestController
@RequestMapping("/api/chat-bot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<ChatbotResponseDto> postMessage(@CurrentMemberId(required = false) Long memberId,
                                                          @RequestBody ChatbotRequest reqDto,
                                                          HttpSession session) {
        ChatbotResponseDto response = chatbotService.postMessage(memberId, reqDto.message(), session);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ChatbotHistoryDto>> getMessages(@CurrentMemberId(required = false) Long memberId,
                                                               HttpSession session) {
        List<ChatbotHistoryDto> response = chatbotService.getMessages(memberId, session);
        return ResponseEntity.ok(response);
    }
}
