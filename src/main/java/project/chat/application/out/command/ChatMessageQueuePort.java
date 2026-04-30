package project.chat.application.out.command;

import project.chat.application.out.command.model.ChatMessagePayload;

import java.util.List;

public interface ChatMessageQueuePort {

    List<ChatMessagePayload> loadPendingMessages();

    void completePendingMessages();
}
