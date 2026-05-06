package project.chatbot.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.chatbot.application.in.command.AskChatbotUseCase;
import project.chatbot.application.in.command.model.AskChatbotCommand;
import project.chatbot.application.in.command.model.AskChatbotResult;
import project.chatbot.application.out.ai.GenerateChatbotResponsePort;
import project.chatbot.application.out.ai.model.GenerateChatbotResponseCommand;

@Service
@RequiredArgsConstructor
public class ChatbotCommandService implements AskChatbotUseCase {

    private final GenerateChatbotResponsePort generateChatbotResponsePort;

    @Override
    public AskChatbotResult ask(AskChatbotCommand command) {
        return generateChatbotResponsePort.generate(new GenerateChatbotResponseCommand(
                isLogin(command.memberId()),
                command.conversationId(),
                command.message()
        ));
    }

    private boolean isLogin(Long memberId) {
        return memberId != null;
    }
}
