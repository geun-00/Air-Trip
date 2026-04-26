package project.wishlist.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.accommodation.application.out.query.LoadAccommodationWishlistPort;
import project.accommodation.application.out.query.model.WishlistInfoView;
import project.wishlist.adapter.out.persistence.model.WishlistInfoRow;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class WishlistQueryPersistenceAdapter implements LoadAccommodationWishlistPort {

    private final WishlistQueryRepository wishlistQueryRepository;

    @Override
    public Optional<WishlistInfoView> loadWishlistInfo(Long accommodationId, Long memberId) {
        return wishlistQueryRepository.getWishlistInfo(accommodationId, memberId)
                                      .map(this::toView);
    }

    @Override
    public Map<Long, WishlistInfoView> loadWishlistInfos(List<Long> accommodationIds, Long memberId) {
        return wishlistQueryRepository.getWishlistInfos(accommodationIds, memberId)
                                      .stream()
                                      .map(this::toView)
                                      .collect(toMap(
                                              WishlistInfoView::accommodationId,
                                              identity()
                                      ));
    }

    private WishlistInfoView toView(WishlistInfoRow row) {
        return new WishlistInfoView(
                row.accommodationId(),
                row.isInWishlist(),
                row.wishlistId(),
                row.wishlistName()
        );
    }
}
