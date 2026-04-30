package project.chat.application.in.query.model;

import java.util.List;

public record ChatMessagesView(
        List<ChatMessageView> messages,
        boolean hasMore
) {
}
