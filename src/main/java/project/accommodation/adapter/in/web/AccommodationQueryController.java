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
import project.accommodation.application.in.query.ReadAccommodationDetailUseCase;
import project.accommodation.application.in.query.ReadAccommodationsUseCase;
import project.accommodation.application.in.query.model.AccommodationDetailView;
import project.accommodation.application.in.query.model.AccommodationPriceView;
import project.accommodation.application.in.query.model.AccommodationSearchQuery;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.common.adapter.in.web.response.PageResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class AccommodationQueryController {

    private final ReadAccommodationsUseCase readAccommodationsUseCase;
    private final ReadAccommodationDetailUseCase readAccommodationDetailUseCase;

    @GetMapping
    public ResponseEntity<List<MainAccommodationResponse>> getAccommodations(@CurrentMemberId(required = false) Long memberId) {
        List<MainAccommodationResponse> result = readAccommodationsUseCase.getAccommodations(memberId)
                                                                          .stream()
                                                                          .map(AccommodationQueryMapper::toResponse)
                                                                          .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<FilteredAccommodationResponse>> getFilteredPagingAccommodations(
            @ModelAttribute AccommodationSearchRequest searchRequest,
            @CurrentMemberId(required = false) Long memberId,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        AccommodationSearchQuery searchQuery = AccommodationQueryMapper.toQuery(searchRequest, pageable);
        PageResponse<FilteredAccommodationResponse> result = PageResponse.from(
                readAccommodationsUseCase.getFilteredPagingAccommodations(searchQuery, memberId)
                                         .map(AccommodationQueryMapper::toResponse)
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailAccommodationResponse> getAccommodation(
            @PathVariable("id") Long accId,
            @CurrentMemberId(required = false) Long memberId
    ) {
        AccommodationDetailView result = readAccommodationDetailUseCase.getDetailAccommodation(accId, memberId);
        return ResponseEntity.ok(AccommodationQueryMapper.toResponse(result));
    }

    @GetMapping("/{id}/price")
    public ResponseEntity<AccommodationPriceResponse> getAccommodationPrice(
            @PathVariable("id") Long accommodationId,
            @RequestParam("date") LocalDate date
    ) {
        AccommodationPriceView result = readAccommodationsUseCase.getAccommodationPrice(accommodationId, date);
        return ResponseEntity.ok(AccommodationQueryMapper.toResponse(result));
    }
}
