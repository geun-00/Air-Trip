package project.chat.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.chat.adapter.out.redis.model.ChatRequestDocument;
import project.chat.application.in.command.model.ChatRequest;
import project.chat.application.out.command.ChatRequestPort;
import project.chat.application.out.command.model.SaveChatRequestCommand;
import project.chat.domain.exception.ChatExceptions;

@Component
@RequiredArgsConstructor
public class ChatRequestCommandAdapter implements ChatRequestPort {

    private final ChatRequestRepository chatRequestRepository;

    @Override
    public boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId) {
        return chatRequestRepository.existsById(requestId(senderId, receiverId));
    }

    @Override
    public ChatRequest load(String requestId) {
        return chatRequestRepository.findById(requestId)
                                    .map(this::toModel)
                                    .orElseThrow(() -> ChatExceptions.notFoundChatRequest(requestId));
    }

    @Override
    public ChatRequest save(SaveChatRequestCommand request) {
        ChatRequestDocument chatRequest = new ChatRequestDocument(
                requestId(request.senderId(), request.receiverId()),
                request.senderId(),
                request.senderName(),
                request.senderProfileImage(),
                request.receiverId(),
                request.receiverName(),
                request.receiverProfileImage(),
                request.createdAt(),
                request.expiresAt()
        );

        ChatRequestDocument savedRequest = chatRequestRepository.save(chatRequest);
        return toModel(savedRequest);
    }

    @Override
    public void delete(String requestId) {
        chatRequestRepository.deleteById(requestId);
    }

    private String requestId(Long senderId, Long receiverId) {
        return ChatRedisKey.CHAT_REQUEST.format(senderId, receiverId);
    }

    private ChatRequest toModel(ChatRequestDocument document) {
        return new ChatRequest(
                document.getRequestId(),
                document.getSenderId(),
                document.getSenderName(),
                document.getSenderProfileImage(),
                document.getReceiverId(),
                document.getReceiverName(),
                document.getReceiverProfileImage(),
                document.getExpiresAt()
        );
    }
}
