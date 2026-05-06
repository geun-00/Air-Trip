package project.chatbot.application.out.ai;

import project.chatbot.application.in.command.model.AskChatbotResult;
import project.chatbot.application.out.ai.model.GenerateChatbotResponseCommand;

public interface GenerateChatbotResponsePort {

    AskChatbotResult generate(GenerateChatbotResponseCommand command);
}
