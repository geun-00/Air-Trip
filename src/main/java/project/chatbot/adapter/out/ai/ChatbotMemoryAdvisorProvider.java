package project.chatbot.adapter.out.ai;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ChatbotMemoryAdvisorProvider {

    private final CustomMessageChatMemoryAdvisor loginMemoryAdvisor;
    private final CustomMessageChatMemoryAdvisor anonymousMemoryAdvisor;

    public ChatbotMemoryAdvisorProvider(
            @Qualifier("loginMemoryAdvisor") CustomMessageChatMemoryAdvisor loginMemoryAdvisor,
            @Qualifier("anonymousMemoryAdvisor") CustomMessageChatMemoryAdvisor anonymousMemoryAdvisor
    ) {
        this.loginMemoryAdvisor = loginMemoryAdvisor;
        this.anonymousMemoryAdvisor = anonymousMemoryAdvisor;
    }

    public CustomMessageChatMemoryAdvisor get(boolean login) {
        return login ? loginMemoryAdvisor : anonymousMemoryAdvisor;
    }
}
