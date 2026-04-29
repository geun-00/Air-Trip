package project.chat.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.chat.application.out.command.SaveChatMessagePort;
import project.chat.domain.ChatMessage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageCommandPersistenceAdapter implements SaveChatMessagePort {

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void saveAll(List<ChatMessage> messages) {
        chatMessageRepository.saveAll(messages);
    }
}
