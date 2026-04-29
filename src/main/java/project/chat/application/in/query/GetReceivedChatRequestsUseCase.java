package project.chat.application.in.query;

import project.chat.application.in.query.model.ChatRequestView;

import java.util.List;

public interface GetReceivedChatRequestsUseCase {

    List<ChatRequestView> getReceivedChatRequests(Long memberId);
}
