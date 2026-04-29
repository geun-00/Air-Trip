package project.wishlist.adapter.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import project.wishlist.domain.WishlistMemo;

@Converter(autoApply = true)
public class WishlistMemoConverter implements AttributeConverter<WishlistMemo, String> {

    @Override
    public String convertToDatabaseColumn(WishlistMemo attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public WishlistMemo convertToEntityAttribute(String dbData) {
        return new WishlistMemo(dbData);
    }
}
