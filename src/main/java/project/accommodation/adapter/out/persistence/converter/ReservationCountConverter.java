package project.accommodation.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.accommodation.domain.ReservationCount;

@Converter(autoApply = true)
public class ReservationCountConverter implements AttributeConverter<ReservationCount, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ReservationCount attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public ReservationCount convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : new ReservationCount(dbData);
    }
}
