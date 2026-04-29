package project.wishlist.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.out.query.LoadAccommodationPort;
import project.wishlist.application.in.query.GetWishlistDetailsQueryUseCase;
import project.wishlist.application.in.query.GetWishlistsQueryUseCase;
import project.wishlist.application.in.query.model.WishlistDetailView;
import project.wishlist.application.in.query.model.WishlistSummaryView;
import project.wishlist.application.in.query.model.WishlistView;
import project.wishlist.application.out.query.LoadWishlistDetailsPort;
import project.wishlist.application.out.query.LoadWishlistsPort;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistQueryService implements GetWishlistDetailsQueryUseCase,
                                             GetWishlistsQueryUseCase {

    private final LoadWishlistsPort loadWishlistsPort;
    private final LoadWishlistDetailsPort loadWishlistDetailsPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public List<WishlistDetailView> getWishlistDetails(Long wishlistId, Long memberId) {
        return loadWishlistDetailsPort.loadWishlistDetails(wishlistId, memberId);
    }

    @Override
    public List<WishlistView> getWishlists(Long memberId) {
        List<WishlistSummaryView> wishlists = loadWishlistsPort.loadWishlists(memberId);
        Map<Long, String> thumbnailUrls = loadAccommodationPort.loadThumbnailUrls(recentAccommodationIds(wishlists));

        return wishlists.stream()
                        .map(wishlist -> new WishlistView(
                                wishlist.wishlistId(),
                                wishlist.name(),
                                thumbnailUrls.get(wishlist.recentAccommodationId()),
                                wishlist.savedAccommodations()
                        ))
                        .toList();
    }

    private List<Long> recentAccommodationIds(List<WishlistSummaryView> wishlists) {
        return wishlists.stream()
                        .map(WishlistSummaryView::recentAccommodationId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();
    }
}
