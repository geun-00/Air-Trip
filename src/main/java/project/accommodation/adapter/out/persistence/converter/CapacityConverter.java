package project.accommodation.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.accommodation.domain.Capacity;

@Converter(autoApply = true)
public class CapacityConverter implements AttributeConverter<Capacity, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Capacity attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public Capacity convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : new Capacity(dbData);
    }
}
