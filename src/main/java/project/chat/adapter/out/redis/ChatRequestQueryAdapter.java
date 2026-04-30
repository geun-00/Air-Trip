package project.chat.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.chat.adapter.out.redis.model.ChatRequestDocument;
import project.chat.application.in.query.model.ChatRequestView;
import project.chat.application.out.query.LoadChatRequestPort;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatRequestQueryAdapter implements LoadChatRequestPort {

    private final ChatRequestRepository chatRequestRepository;

    @Override
    public List<ChatRequestView> loadReceivedChatRequests(Long memberId) {
        return chatRequestRepository.findByReceiverId(memberId)
                                    .stream()
                                    .map(this::toView)
                                    .toList();
    }

    @Override
    public List<ChatRequestView> loadSentChatRequests(Long memberId) {
        return chatRequestRepository.findBySenderId(memberId)
                                    .stream()
                                    .map(this::toView)
                                    .toList();
    }

    private ChatRequestView toView(ChatRequestDocument document) {
        return new ChatRequestView(
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
