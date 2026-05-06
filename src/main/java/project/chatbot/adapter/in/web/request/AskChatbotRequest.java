package project.chatbot.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;

public record AskChatbotRequest(@NotBlank String message) {
}
