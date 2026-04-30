package project.chat.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.chat.domain.ChatMessageContent;

@Converter(autoApply = true)
public class ChatMessageContentConverter implements AttributeConverter<ChatMessageContent, String> {

    @Override
    public String convertToDatabaseColumn(ChatMessageContent attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public ChatMessageContent convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new ChatMessageContent(dbData);
    }
}
