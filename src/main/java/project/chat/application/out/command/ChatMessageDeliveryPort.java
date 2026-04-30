package project.chat.application.out.command;

import project.chat.application.out.command.model.ChatMessagePayload;

public interface ChatMessageDeliveryPort {

    void deliver(ChatMessagePayload message);
}
