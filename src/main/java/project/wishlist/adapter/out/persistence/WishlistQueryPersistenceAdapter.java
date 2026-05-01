package project.wishlist.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.accommodation.adapter.out.persistence.model.AccommodationImageRow;
import project.accommodation.application.out.query.ReadAccommodationWishlistPort;
import project.accommodation.application.out.query.model.WishlistInfoView;
import project.wishlist.adapter.out.persistence.model.WishlistDetailRow;
import project.wishlist.adapter.out.persistence.model.WishlistInfoRow;
import project.wishlist.adapter.out.persistence.model.WishlistsRow;
import project.wishlist.application.in.query.model.WishlistDetailView;
import project.wishlist.application.in.query.model.WishlistSummaryView;
import project.wishlist.application.out.query.LoadWishlistDetailsPort;
import project.wishlist.application.out.query.LoadWishlistsPort;
import project.wishlist.domain.WishlistMemo;
import project.wishlist.domain.WishlistName;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class WishlistQueryPersistenceAdapter implements ReadAccommodationWishlistPort,
                                                        LoadWishlistDetailsPort,
                                                        LoadWishlistsPort {

    private final WishlistQueryRepository wishlistQueryRepository;

    @Override
    public Optional<WishlistInfoView> findByAccommodationIdAndMemberId(Long accommodationId, Long memberId) {
        return wishlistQueryRepository.getWishlistInfo(accommodationId, memberId)
                                      .map(this::toView);
    }

    @Override
    public Map<Long, WishlistInfoView> getAllByAccommodationIdsAndMemberId(List<Long> accommodationIds, Long memberId) {
        return wishlistQueryRepository.getWishlistInfos(accommodationIds, memberId)
                                      .stream()
                                      .map(this::toView)
                                      .collect(toMap(WishlistInfoView::accommodationId, identity()));
    }

    private WishlistInfoView toView(WishlistInfoRow row) {
        return new WishlistInfoView(
                row.accommodationId(),
                row.isInWishlist(),
                row.wishlistId(),
                value(row.wishlistName())
        );
    }

    @Override
    public List<WishlistDetailView> loadWishlistDetails(Long wishlistId, Long memberId) {
        List<WishlistDetailRow> rows = wishlistQueryRepository.findWishlistDetails(wishlistId, memberId);
        List<Long> accommodationIds = rows.stream()
                                          .map(WishlistDetailRow::accommodationId)
                                          .toList();

        Map<Long, List<String>> imagesMap = wishlistQueryRepository.findAllImages(accommodationIds)
                                                                   .stream()
                                                                   .collect(groupingBy(
                                                                           AccommodationImageRow::accommodationId,
                                                                           mapping(
                                                                                   AccommodationImageRow::imageUrl,
                                                                                   toList()
                                                                           )
                                                                   ));
        return rows.stream()
                   .map(row -> toView(
                           row,
                           imagesMap.getOrDefault(row.accommodationId(), List.of())
                   ))
                   .toList();
    }

    private WishlistDetailView toView(WishlistDetailRow row, List<String> imageUrls) {
        return new WishlistDetailView(
                row.accommodationId(),
                value(row.wishlistName()),
                row.title(),
                row.description(),
                row.mapX(),
                row.mapY(),
                row.avgRate(),
                imageUrls,
                value(row.memo())
        );
    }

    @Override
    public List<WishlistSummaryView> loadWishlists(Long memberId) {
        return wishlistQueryRepository.getAllWishlists(memberId)
                                      .stream()
                                      .map(this::toView)
                                      .toList();
    }

    private WishlistSummaryView toView(WishlistsRow row) {
        return new WishlistSummaryView(
                row.wishlistId(),
                value(row.name()),
                row.recentAccommodationId(),
                row.savedAccommodations()
        );
    }

    private String value(WishlistName name) {
        return name == null ? null : name.value();
    }

    private String value(WishlistMemo memo) {
        return memo == null ? null : memo.value();
    }
}
