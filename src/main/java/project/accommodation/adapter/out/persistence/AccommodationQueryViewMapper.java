package project.accommodation.adapter.out.persistence;

import project.accommodation.adapter.out.persistence.model.FilteredAccommodationRow;
import project.accommodation.adapter.out.persistence.model.MainAccommodationRow;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.in.query.model.MainAccommodationItemView;
import project.accommodation.application.in.query.model.MainAccommodationView;
import project.wishlist.domain.WishlistName;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

final class AccommodationQueryViewMapper {

    private AccommodationQueryViewMapper() {
    }

    static List<MainAccommodationView> toMainViews(List<MainAccommodationRow> rows) {
        return rows.stream()
                   .map(AccommodationQueryViewMapper::toMainItemView)
                   .collect(
                           groupingBy(
                                   item -> new AreaKey(item.areaName(), item.areaCode()),
                                   mapping(item -> item, toList())
                           )
                   )
                   .entrySet()
                   .stream()
                   .map(entry -> new MainAccommodationView(
                           entry.getKey().areaName(),
                           entry.getKey().areaCode(),
                           entry.getValue()
                   ))
                   .toList();
    }

    private static MainAccommodationItemView toMainItemView(MainAccommodationRow row) {
        return new MainAccommodationItemView(
                row.accommodationId(),
                row.title(),
                row.price(),
                row.avgRate(),
                row.thumbnailUrl(),
                row.isInWishlist(),
                value(row.wishlistName()),
                row.wishlistId(),
                row.areaName(),
                row.areaCode()
        );
    }

    static FilteredAccommodationView toFilteredView(FilteredAccommodationRow row) {
        return new FilteredAccommodationView(
                row.accommodationId(),
                row.title(),
                row.price(),
                row.avgRate(),
                row.reviewCount(),
                row.imageUrls(),
                row.isInWishlist(),
                row.wishlistId(),
                value(row.wishlistName())
        );
    }

    private static String value(WishlistName name) {
        return name == null ? null : name.value();
    }

    private record AreaKey(String areaName, String areaCode) {
    }
}
