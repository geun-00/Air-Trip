package project.member.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.member.domain.PhoneNumber;

@Converter(autoApply = true)
public class PhoneNumberConverter implements AttributeConverter<PhoneNumber, String> {

    @Override
    public String convertToDatabaseColumn(PhoneNumber attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public PhoneNumber convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new PhoneNumber(dbData);
    }
}
