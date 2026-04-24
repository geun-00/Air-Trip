package project.accommodation.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.accommodation.adapter.in.web.request.AccommodationSearchCondition;
import project.accommodation.adapter.in.web.response.AccommodationPriceResDto;
import project.accommodation.adapter.in.web.response.DetailAccommodationResDto;
import project.accommodation.adapter.in.web.response.FilteredAccListResDto;
import project.accommodation.adapter.in.web.response.MainAccResDto;
import project.accommodation.adapter.in.web.response.ViewHistoryResDto;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.common.adapter.in.web.response.PageResponse;
import project.accommodation.application.service.AccommodationService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;

    @GetMapping
    public ResponseEntity<List<MainAccResDto>> getAccommodations(@CurrentMemberId(required = false) Long memberId) {
        List<MainAccResDto> result = accommodationService.getAccommodations(memberId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<FilteredAccListResDto>> getFilteredPagingAccommodations(@ModelAttribute AccommodationSearchCondition searchDto,
                                                                                               @CurrentMemberId(required = false) Long memberId,
                                                                                               Pageable pageable) {
        PageResponse<FilteredAccListResDto> result = accommodationService.getFilteredPagingAccommodations(searchDto, memberId, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailAccommodationResDto> getAccommodation(@PathVariable("id") Long accId,
                                                                      @CurrentMemberId(required = false) Long memberId) {
        DetailAccommodationResDto result = accommodationService.getDetailAccommodation(accId, memberId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/price")
    public ResponseEntity<AccommodationPriceResDto> getAccommodationPrice(@PathVariable("id") Long accId,
                                                                          @RequestParam("date") LocalDate date) {
        AccommodationPriceResDto result = accommodationService.getAccommodationPrice(accId, date);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ViewHistoryResDto>> getRecentViewAccommodations(@CurrentMemberId Long memberId) {
        List<ViewHistoryResDto> result = accommodationService.getRecentViewAccommodations(memberId);
        return ResponseEntity.ok(result);
    }
}
