package project.wishlist.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.wishlist.domain.WishlistName;

@Converter(autoApply = true)
public class WishlistNameConverter implements AttributeConverter<WishlistName, String> {

    @Override
    public String convertToDatabaseColumn(WishlistName attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public WishlistName convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new WishlistName(dbData);
    }
}
