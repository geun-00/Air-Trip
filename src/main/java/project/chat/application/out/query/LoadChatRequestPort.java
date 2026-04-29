package project.chat.application.out.query;

import project.chat.application.in.query.model.ChatRequestView;

import java.util.List;

public interface LoadChatRequestPort {

    List<ChatRequestView> loadReceivedChatRequests(Long memberId);

    List<ChatRequestView> loadSentChatRequests(Long memberId);
}
