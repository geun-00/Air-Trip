package project.chat.application.out.command;

import project.chat.domain.ChatMessage;

import java.util.List;

public interface SaveChatMessagePort {

    void saveAll(List<ChatMessage> messages);
}
