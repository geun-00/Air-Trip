package project.chat.application.event;

import project.chat.application.in.command.model.ChatRequest;

public record ChatRequestCreatedEvent(ChatRequest chatRequest) {
}
