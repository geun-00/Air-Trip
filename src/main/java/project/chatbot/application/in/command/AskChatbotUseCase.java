package project.chatbot.application.in.command;

import project.chatbot.application.in.command.model.AskChatbotCommand;
import project.chatbot.application.in.command.model.AskChatbotResult;

public interface AskChatbotUseCase {

    AskChatbotResult ask(AskChatbotCommand command);
}
