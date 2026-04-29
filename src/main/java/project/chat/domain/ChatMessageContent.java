package project.chat.domain;

public record ChatMessageContent(String value) {

    public ChatMessageContent {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("chat message content must not be blank");
        }

        if (value.length() > 1000) {
            throw new IllegalArgumentException("chat message content must be 1000 characters or less");
        }
    }
}
