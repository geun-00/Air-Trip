package project.accommodation.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.accommodation.adapter.in.web.request.AccommodationSearchRequest;
import project.accommodation.adapter.in.web.response.AccommodationPriceResponse;
import project.accommodation.adapter.in.web.response.DetailAccommodationResponse;
import project.accommodation.adapter.in.web.response.FilteredAccommodationResponse;
import project.accommodation.adapter.in.web.response.MainAccommodationResponse;
import project.accommodation.adapter.in.web.response.MainAccommodationsResponse;
import project.accommodation.application.in.query.GetAccommodationDetailQueryUseCase;
import project.accommodation.application.in.query.GetAccommodationPriceQueryUseCase;
import project.accommodation.application.in.query.GetMainAccommodationsQueryUseCase;
import project.accommodation.application.in.query.SearchAccommodationsQueryUseCase;
import project.accommodation.application.in.query.model.AccommodationDetailView;
import project.accommodation.application.in.query.model.AccommodationPriceView;
import project.accommodation.application.in.query.model.AccommodationSearchQuery;
import project.accommodation.application.in.query.model.DetailImageView;
import project.accommodation.application.in.query.model.DetailReviewView;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.in.query.model.MainAccommodationItemView;
import project.accommodation.application.in.query.model.MainAccommodationView;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.common.adapter.in.web.response.PageResponse;

import java.time.LocalDate;
import java.util.List;

import static project.accommodation.adapter.in.web.response.DetailAccommodationResponse.DetailImageResponse;
import static project.accommodation.adapter.in.web.response.DetailAccommodationResponse.DetailReviewResponse;
import static project.accommodation.adapter.in.web.response.DetailAccommodationResponse.ReservedDateResponse;
import static project.accommodation.application.in.query.model.AccommodationDetailView.ReservedDateView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class AccommodationQueryController {

    private final SearchAccommodationsQueryUseCase searchAccommodationsQueryUseCase;
    private final GetMainAccommodationsQueryUseCase getMainAccommodationsQueryUseCase;
    private final GetAccommodationPriceQueryUseCase getAccommodationPriceQueryUseCase;
    private final GetAccommodationDetailQueryUseCase getAccommodationDetailQueryUseCase;

    @GetMapping
    public ResponseEntity<List<MainAccommodationResponse>> getAccommodations(@CurrentMemberId(required = false) Long memberId) {
        List<MainAccommodationResponse> result = getMainAccommodationsQueryUseCase.getAccommodations(memberId)
                                                                                  .stream()
                                                                                  .map(this::toResponse)
                                                                                  .toList();
        return ResponseEntity.ok(result);
    }

    private MainAccommodationResponse toResponse(MainAccommodationView view) {
        return new MainAccommodationResponse(
                view.areaName(),
                view.areaCode(),
                view.accommodations()
                    .stream()
                    .map(this::toResponse)
                    .toList()
        );
    }

    private MainAccommodationsResponse toResponse(MainAccommodationItemView view) {
        return new MainAccommodationsResponse(
                view.accommodationId(),
                view.title(),
                view.price(),
                view.avgRate(),
                view.thumbnailUrl(),
                view.isInWishlist(),
                view.wishlistName(),
                view.wishlistId()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<FilteredAccommodationResponse>> getFilteredPagingAccommodations(
            @ModelAttribute AccommodationSearchRequest searchRequest,
            @CurrentMemberId(required = false) Long memberId,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        AccommodationSearchQuery searchQuery = new AccommodationSearchQuery(
                searchRequest.areaCode(),
                searchRequest.amenities(),
                searchRequest.priceGoe(),
                searchRequest.priceLoe(),
                pageable
        );
        PageResponse<FilteredAccommodationResponse> result = searchAccommodationsQueryUseCase.getFilteredPagingAccommodations(searchQuery, memberId)
                                                                                             .map(this::toResponse);
        return ResponseEntity.ok(result);
    }

    private FilteredAccommodationResponse toResponse(FilteredAccommodationView view) {
        return new FilteredAccommodationResponse(
                view.accommodationId(),
                view.title(),
                view.price(),
                view.avgRate(),
                view.reviewCount(),
                view.imageUrls(),
                view.isInWishlist(),
                view.wishlistId(),
                view.wishlistName()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailAccommodationResponse> getAccommodation(
            @PathVariable("id") Long accId,
            @CurrentMemberId(required = false) Long memberId
    ) {
        AccommodationDetailView result = getAccommodationDetailQueryUseCase.getDetailAccommodation(accId, memberId);
        return ResponseEntity.ok(toResponse(result));
    }

    private DetailAccommodationResponse toResponse(AccommodationDetailView view) {
        return new DetailAccommodationResponse(
                view.accommodationId(),
                view.title(),
                view.maxPeople(),
                view.address(),
                view.mapX(),
                view.mapY(),
                view.checkIn(),
                view.checkOut(),
                view.description(),
                view.number(),
                view.refundRegulation(),
                view.price(),
                view.isInWishlist(),
                view.wishlistId(),
                view.wishlistName(),
                view.avgRate(),
                toResponse(view.images()),
                view.amenities(),
                view.reviews().stream().map(this::toResponse).toList(),
                view.reservedDates().stream().map(this::toResponse).toList()
        );
    }

    private DetailImageResponse toResponse(DetailImageView view) {
        return new DetailImageResponse(view.thumbnail(), view.others());
    }

    private DetailReviewResponse toResponse(DetailReviewView view) {
        return new DetailReviewResponse(
                view.memberId(),
                view.memberName(),
                view.profileUrl(),
                view.memberCreatedDate(),
                view.reviewCreatedDate(),
                view.rating(),
                view.content()
        );
    }

    private ReservedDateResponse toResponse(ReservedDateView view) {
        return new ReservedDateResponse(view.start(), view.end());
    }

    @GetMapping("/{id}/price")
    public ResponseEntity<AccommodationPriceResponse> getAccommodationPrice(
            @PathVariable("id") Long accommodationId,
            @RequestParam("date") LocalDate date
    ) {
        AccommodationPriceView result = getAccommodationPriceQueryUseCase.getAccommodationPrice(accommodationId, date);
        return ResponseEntity.ok(toResponse(result));
    }

    private AccommodationPriceResponse toResponse(AccommodationPriceView view) {
        return new AccommodationPriceResponse(view.accommodationId(), view.date(), view.price());
    }
}
