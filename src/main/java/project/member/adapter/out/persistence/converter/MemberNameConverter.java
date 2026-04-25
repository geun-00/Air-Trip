package project.member.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.member.domain.MemberName;

@Converter(autoApply = true)
public class MemberNameConverter implements AttributeConverter<MemberName, String> {

    @Override
    public String convertToDatabaseColumn(MemberName attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public MemberName convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new MemberName(dbData);
    }
}
