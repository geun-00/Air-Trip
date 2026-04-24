package project.wishlist.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.wishlist.adapter.in.web.request.AddAccToWishlistRequest;
import project.wishlist.adapter.in.web.request.MemoUpdateRequest;
import project.wishlist.adapter.in.web.request.WishlistCreateRequest;
import project.wishlist.adapter.in.web.request.WishlistUpdateRequest;
import project.wishlist.adapter.in.web.response.WishlistCreateResponse;
import project.wishlist.adapter.in.web.response.WishlistDetailResponse;
import project.wishlist.adapter.in.web.response.WishlistsResponse;
import project.wishlist.application.service.WishlistService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<WishlistCreateResponse> createWishlist(@Valid @RequestBody WishlistCreateRequest reqDto,
                                                                 @CurrentMemberId Long memberId) {
        WishlistCreateResponse resDto = wishlistService.createWishlist(reqDto, memberId);
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

    @PostMapping("/{wishlistId}/accommodations")
    public ResponseEntity<?> addAccommodation(@PathVariable("wishlistId") Long wishlistId,
                                              @RequestBody AddAccToWishlistRequest reqDto,
                                              @CurrentMemberId Long memberId) {
        wishlistService.addAccommodationToWishlist(wishlistId, reqDto, memberId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{wishlistId}/accommodations/{accommodationId}")
    public ResponseEntity<?> updateMemo(@PathVariable("wishlistId") Long wishlistId,
                                        @PathVariable("accommodationId") Long accommodationId,
                                        @Valid @RequestBody MemoUpdateRequest reqDto,
                                        @CurrentMemberId Long memberId) {
        wishlistService.updateMemo(wishlistId, accommodationId, memberId, reqDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{wishlistId}/accommodations/{accommodationId}")
    public ResponseEntity<?> removeAccommodation(@PathVariable("wishlistId") Long wishlistId,
                                                 @PathVariable("accommodationId") Long accommodationId,
                                                 @CurrentMemberId Long memberId) {
        wishlistService.removeAccommodationFromWishlist(wishlistId, accommodationId, memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{wishlistId}")
    public ResponseEntity<?> updateWishlistName(@PathVariable("wishlistId") Long wishlistId,
                                                @Valid @RequestBody WishlistUpdateRequest reqDto,
                                                @CurrentMemberId Long memberId) {
        wishlistService.updateWishlistName(wishlistId, reqDto, memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<?> removeWishlist(@PathVariable("wishlistId") Long wishlistId,
                                            @CurrentMemberId Long memberId) {
        wishlistService.removeWishlist(wishlistId, memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{wishlistId}")
    public ResponseEntity<List<WishlistDetailResponse>> getAccommodationsFromWishlist(@PathVariable("wishlistId") Long wishlistId,
                                                                                      @CurrentMemberId Long memberId) {
        List<WishlistDetailResponse> result = wishlistService.getAccommodationsFromWishlist(wishlistId, memberId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<WishlistsResponse>> getAllWishlists(@CurrentMemberId Long memberId) {
        List<WishlistsResponse> result = wishlistService.getAllWishlists(memberId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
