package project.chatbot.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.chatbot.application.in.query.GetChatbotMessagesUseCase;
import project.chatbot.application.in.query.model.ChatbotHistoryQuery;
import project.chatbot.application.in.query.model.ChatbotMessageView;
import project.chatbot.application.out.query.LoadChatbotHistoryPort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotQueryService implements GetChatbotMessagesUseCase {

    private final LoadChatbotHistoryPort loadChatbotHistoryPort;

    @Override
    public List<ChatbotMessageView> getMessages(ChatbotHistoryQuery query) {
        return loadChatbotHistoryPort.getMessages(query.conversationId());
    }
}
