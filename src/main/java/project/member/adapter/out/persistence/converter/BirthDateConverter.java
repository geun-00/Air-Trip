package project.member.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.member.domain.BirthDate;

import java.time.LocalDate;

@Converter(autoApply = true)
public class BirthDateConverter implements AttributeConverter<BirthDate, LocalDate> {

    @Override
    public LocalDate convertToDatabaseColumn(BirthDate attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public BirthDate convertToEntityAttribute(LocalDate dbData) {
        return dbData == null ? null : new BirthDate(dbData);
    }
}
