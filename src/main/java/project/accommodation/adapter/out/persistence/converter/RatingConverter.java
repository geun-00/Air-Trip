package project.accommodation.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.accommodation.domain.Rating;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, Double> {

    @Override
    public Double convertToDatabaseColumn(Rating attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public Rating convertToEntityAttribute(Double dbData) {
        return dbData == null ? null : new Rating(dbData);
    }
}
