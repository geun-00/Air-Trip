package project.member.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.member.domain.AboutMe;

@Converter(autoApply = true)
public class AboutMeConverter implements AttributeConverter<AboutMe, String> {

    @Override
    public String convertToDatabaseColumn(AboutMe attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public AboutMe convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new AboutMe(dbData);
    }
}
