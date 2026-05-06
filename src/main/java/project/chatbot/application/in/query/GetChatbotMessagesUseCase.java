package project.chatbot.application.in.query;

import project.chatbot.application.in.query.model.ChatbotHistoryQuery;
import project.chatbot.application.in.query.model.ChatbotMessageView;

import java.util.List;

public interface GetChatbotMessagesUseCase {

    List<ChatbotMessageView> getMessages(ChatbotHistoryQuery query);
}
