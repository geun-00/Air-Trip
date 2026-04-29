package project.chat.adapter.in.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import project.chat.adapter.in.websocket.request.ChatMessageRequest;
import project.chat.application.in.command.SendChatMessageUseCase;
import project.chat.application.in.command.model.SendChatMessageCommand;

@Controller
@RequiredArgsConstructor
public class StompController {

    private final SendChatMessageUseCase sendChatMessageUseCase;

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable("roomId") Long roomId, ChatMessageRequest request) {
        sendChatMessageUseCase.sendMessage(new SendChatMessageCommand(
                roomId,
                request.senderId(),
                request.content()
        ));
    }
}
