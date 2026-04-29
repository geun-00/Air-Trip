package project.chat.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.chat.domain.ChatRoomName;

@Converter(autoApply = true)
public class ChatRoomNameConverter implements AttributeConverter<ChatRoomName, String> {

    @Override
    public String convertToDatabaseColumn(ChatRoomName attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public ChatRoomName convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new ChatRoomName(dbData);
    }
}
