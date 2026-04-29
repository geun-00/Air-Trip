package project.wishlist.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.wishlist.adapter.in.web.response.WishlistDetailResponse;
import project.wishlist.adapter.in.web.response.WishlistsResponse;
import project.wishlist.application.in.query.GetWishlistDetailsQueryUseCase;
import project.wishlist.application.in.query.GetWishlistsQueryUseCase;
import project.wishlist.application.in.query.model.WishlistDetailView;
import project.wishlist.application.in.query.model.WishlistView;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlists")
public class WishlistQueryController {

    private final GetWishlistsQueryUseCase getWishlistsQueryUseCase;
    private final GetWishlistDetailsQueryUseCase getWishlistDetailsQueryUseCase;

    @GetMapping("/{wishlistId}")
    public ResponseEntity<List<WishlistDetailResponse>> getAccommodationsFromWishlist(
            @PathVariable Long wishlistId,
            @CurrentMemberId Long memberId
    ) {
        List<WishlistDetailResponse> result = getWishlistDetailsQueryUseCase.getWishlistDetails(wishlistId, memberId)
                                                                            .stream()
                                                                            .map(this::toResponse)
                                                                            .toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private WishlistDetailResponse toResponse(WishlistDetailView view) {
        return new WishlistDetailResponse(
                view.accommodationId(),
                view.wishlistName(),
                view.title(),
                view.description(),
                view.mapX(),
                view.mapY(),
                view.avgRate(),
                view.imageUrls(),
                view.memo()
        );
    }

    @GetMapping
    public ResponseEntity<List<WishlistsResponse>> getAllWishlists(@CurrentMemberId Long memberId) {
        List<WishlistsResponse> result = getWishlistsQueryUseCase.getWishlists(memberId)
                                                                 .stream()
                                                                 .map(this::toResponse)
                                                                 .toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private WishlistsResponse toResponse(WishlistView view) {
        return new WishlistsResponse(
                view.wishlistId(),
                view.name(),
                view.thumbnailUrl(),
                view.savedAccommodations()
        );
    }
}
