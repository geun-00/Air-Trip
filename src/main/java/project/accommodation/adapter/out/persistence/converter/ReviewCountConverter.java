package project.accommodation.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.accommodation.domain.ReviewCount;

@Converter(autoApply = true)
public class ReviewCountConverter implements AttributeConverter<ReviewCount, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ReviewCount attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public ReviewCount convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : new ReviewCount(dbData);
    }
}
