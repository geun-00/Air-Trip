package project.chat.adapter.in.web.request;

import jakarta.validation.constraints.NotNull;

public record RequestChatRequest(@NotNull Long receiverId) {
}
