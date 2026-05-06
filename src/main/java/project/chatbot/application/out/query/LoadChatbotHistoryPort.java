package project.chatbot.application.out.query;

import project.chatbot.application.in.query.model.ChatbotMessageView;

import java.util.List;

public interface LoadChatbotHistoryPort {

    List<ChatbotMessageView> getMessages(String conversationId);
}
