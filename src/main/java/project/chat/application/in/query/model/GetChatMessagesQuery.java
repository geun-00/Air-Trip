package project.chat.application.in.query.model;

public record GetChatMessagesQuery(
        Long lastMessageId,
        Long roomId,
        int pageSize
) {
}
