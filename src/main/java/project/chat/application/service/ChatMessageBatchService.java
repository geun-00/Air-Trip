package project.chat.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.chat.application.out.command.ChatMessageQueuePort;
import project.chat.application.out.command.SaveChatMessagePort;
import project.chat.application.out.command.model.ChatMessagePayload;
import project.chat.domain.ChatMessage;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageBatchService {

    private final SaveChatMessagePort saveChatMessagePort;
    private final ChatMessageQueuePort chatMessageQueuePort;

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void flushMessagesToDB() {
        List<ChatMessagePayload> pendingMessages = chatMessageQueuePort.loadPendingMessages();
        if (pendingMessages.isEmpty()) {
            return;
        }

        List<ChatMessage> messages = pendingMessages.stream()
                                                    .filter(message -> !message.left())
                                                    .map(message -> ChatMessage.create(
                                                            message.roomId(),
                                                            message.senderId(),
                                                            message.content()
                                                    ))
                                                    .toList();
        if (!messages.isEmpty()) {
            saveChatMessagePort.saveAll(messages);
        }

        chatMessageQueuePort.completePendingMessages();
    }
}
