package project.chat.application.event;

import project.chat.adapter.out.redis.model.ChatRequest;

public record ChatRequestCreatedEvent(ChatRequest chatRequest) {
}
