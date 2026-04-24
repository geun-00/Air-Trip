package project.chat.adapter.in.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import project.chat.adapter.in.websocket.request.ChatMessageRequest;
import project.chat.application.service.ChatMessageService;
import project.chat.application.service.ChatRoomService;

@Controller
@RequiredArgsConstructor
public class StompController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable("roomId") Long roomId, ChatMessageRequest chatMessageDto) {
        Long senderId = chatMessageDto.senderId();

        chatRoomService.validateMessageDelivery(roomId, senderId);
        chatMessageService.handleMessagePostProcess(roomId, chatMessageDto);
    }
}
