package project.wishlist.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.wishlist.adapter.in.web.request.AddAccommodationToWishlistRequest;
import project.wishlist.adapter.in.web.request.MemoUpdateRequest;
import project.wishlist.adapter.in.web.request.WishlistCreateRequest;
import project.wishlist.adapter.in.web.request.WishlistUpdateRequest;
import project.wishlist.adapter.in.web.response.WishlistCreateResponse;
import project.wishlist.application.in.command.AddAccommodationToWishlistUseCase;
import project.wishlist.application.in.command.CreateWishlistUseCase;
import project.wishlist.application.in.command.RemoveAccommodationFromWishlistUseCase;
import project.wishlist.application.in.command.RemoveWishlistUseCase;
import project.wishlist.application.in.command.UpdateWishlistMemoUseCase;
import project.wishlist.application.in.command.UpdateWishlistNameUseCase;
import project.wishlist.application.in.command.model.AddAccommodationToWishlistCommand;
import project.wishlist.application.in.command.model.CreateWishlistCommand;
import project.wishlist.application.in.command.model.CreateWishlistResult;
import project.wishlist.application.in.command.model.RemoveAccommodationFromWishlistCommand;
import project.wishlist.application.in.command.model.RemoveWishlistCommand;
import project.wishlist.application.in.command.model.UpdateWishlistMemoCommand;
import project.wishlist.application.in.command.model.UpdateWishlistNameCommand;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlists")
public class WishlistCommandController {

    private final CreateWishlistUseCase createWishlistUseCase;
    private final RemoveWishlistUseCase removeWishlistUseCase;
    private final UpdateWishlistMemoUseCase updateWishlistMemoUseCase;
    private final UpdateWishlistNameUseCase updateWishlistNameUseCase;
    private final AddAccommodationToWishlistUseCase addAccommodationToWishlistUseCase;
    private final RemoveAccommodationFromWishlistUseCase removeAccommodationFromWishlistUseCase;

    @PostMapping
    public ResponseEntity<WishlistCreateResponse> createWishlist(
            @Valid @RequestBody WishlistCreateRequest request,
            @CurrentMemberId Long memberId
    ) {
        CreateWishlistResult result = createWishlistUseCase.createWishlist(new CreateWishlistCommand(
                memberId,
                request.wishlistName()
        ));

        return new ResponseEntity<>(toResponse(result), HttpStatus.CREATED);
    }

    private WishlistCreateResponse toResponse(CreateWishlistResult result) {
        return new WishlistCreateResponse(result.wishlistId(), result.wishlistName());
    }

    @PostMapping("/{wishlistId}/accommodations")
    public ResponseEntity<Void> addAccommodation(
            @PathVariable Long wishlistId,
            @RequestBody AddAccommodationToWishlistRequest request,
            @CurrentMemberId Long memberId
    ) {
        addAccommodationToWishlistUseCase.addAccommodationToWishlist(new AddAccommodationToWishlistCommand(
                wishlistId,
                request.accommodationId(),
                memberId
        ));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{wishlistId}/accommodations/{accommodationId}")
    public ResponseEntity<Void> updateMemo(
            @PathVariable Long wishlistId,
            @PathVariable Long accommodationId,
            @Valid @RequestBody MemoUpdateRequest request,
            @CurrentMemberId Long memberId
    ) {
        updateWishlistMemoUseCase.updateMemo(new UpdateWishlistMemoCommand(
                wishlistId,
                accommodationId,
                memberId,
                request.memo()
        ));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{wishlistId}/accommodations/{accommodationId}")
    public ResponseEntity<Void> removeAccommodation(
            @PathVariable Long wishlistId,
            @PathVariable Long accommodationId,
            @CurrentMemberId Long memberId
    ) {
        removeAccommodationFromWishlistUseCase.removeAccommodationFromWishlist(new RemoveAccommodationFromWishlistCommand(
                wishlistId,
                accommodationId,
                memberId
        ));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{wishlistId}")
    public ResponseEntity<Void> updateWishlistName(
            @PathVariable Long wishlistId,
            @Valid @RequestBody WishlistUpdateRequest request,
            @CurrentMemberId Long memberId
    ) {
        updateWishlistNameUseCase.updateWishlistName(new UpdateWishlistNameCommand(
                wishlistId,
                memberId,
                request.wishlistName()
        ));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<Void> removeWishlist(
            @PathVariable Long wishlistId,
            @CurrentMemberId Long memberId
    ) {
        removeWishlistUseCase.removeWishlist(new RemoveWishlistCommand(wishlistId, memberId));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
