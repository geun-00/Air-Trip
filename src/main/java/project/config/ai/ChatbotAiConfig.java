package project.config.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.chatbot.adapter.out.ai.CustomMessageChatMemoryAdvisor;
import project.chatbot.application.out.command.SaveChatbotHistoryPort;
import project.chatbot.application.out.query.LoadChatbotHistoryPort;

@Configuration
public class ChatbotAiConfig {

    @Bean
    public ChatClient openAiChatClient(ChatModel openAiChatModel) {
        return ChatClient.create(openAiChatModel);
    }

    @Bean
    public ChatMemory loginChatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                                      .chatMemoryRepository(jdbcChatMemoryRepository)
                                      .build();
    }

    @Bean
    public ChatMemory anonymousChatMemory() {
        return MessageWindowChatMemory.builder().build();
    }

    @Bean
    public CustomMessageChatMemoryAdvisor loginMemoryAdvisor(
            ChatMemory loginChatMemory,
            SaveChatbotHistoryPort saveChatbotHistoryPort,
            LoadChatbotHistoryPort loadChatbotHistoryPort
    ) {
        return CustomMessageChatMemoryAdvisor.builder(loginChatMemory)
                                             .chatbotHistoryMemory(saveChatbotHistoryPort, loadChatbotHistoryPort)
                                             .build();
    }

    @Bean
    public CustomMessageChatMemoryAdvisor anonymousMemoryAdvisor(
            ChatMemory anonymousChatMemory,
            SaveChatbotHistoryPort saveChatbotHistoryPort,
            LoadChatbotHistoryPort loadChatbotHistoryPort
    ) {
        return CustomMessageChatMemoryAdvisor.builder(anonymousChatMemory)
                                             .chatbotHistoryMemory(saveChatbotHistoryPort, loadChatbotHistoryPort)
                                             .build();
    }
}
