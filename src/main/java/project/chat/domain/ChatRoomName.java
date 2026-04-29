package project.chat.domain;

public record ChatRoomName(String value) {

    public ChatRoomName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("chat room name must not be blank");
        }

        value = value.trim();

        if (value.length() > 255) {
            throw new IllegalArgumentException("chat room name must be 255 characters or less");
        }
    }
}
