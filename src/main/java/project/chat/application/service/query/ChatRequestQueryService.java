package project.chat.application.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.chat.application.in.query.GetReceivedChatRequestsUseCase;
import project.chat.application.in.query.GetSentChatRequestsUseCase;
import project.chat.application.in.query.model.ChatRequestView;
import project.chat.application.out.query.LoadChatRequestPort;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRequestQueryService implements GetReceivedChatRequestsUseCase, GetSentChatRequestsUseCase {

    private final LoadChatRequestPort loadChatRequestPort;

    @Override
    public List<ChatRequestView> getReceivedChatRequests(Long memberId) {
        return loadChatRequestPort.loadReceivedChatRequests(memberId);
    }

    @Override
    public List<ChatRequestView> getSentChatRequests(Long memberId) {
        return loadChatRequestPort.loadSentChatRequests(memberId);
    }
}
