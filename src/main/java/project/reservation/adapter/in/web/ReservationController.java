package project.reservation.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.reservation.adapter.in.web.request.PostReservationRequest;
import project.reservation.adapter.in.web.response.PostReservationResponse;
import project.review.adapter.in.web.request.PostReviewReqDto;
import project.reservation.application.service.ReservationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{accommodationId}")
    public ResponseEntity<PostReservationResponse> postReservation(@CurrentMemberId Long memberId,
                                                                   @PathVariable("accommodationId") Long accommodationId,
                                                                   @RequestBody PostReservationRequest reqDto) {
        PostReservationResponse response = reservationService.postReservation(memberId, accommodationId, reqDto);
        return ResponseEntity.ok(response);
    }

    // TODO : Review 도메인으로 이동
    @PostMapping("/{reservationId}/reviews")
    public ResponseEntity<?> postReview(@PathVariable("reservationId") Long reservationId,
                                        @Valid @RequestBody PostReviewReqDto reqDto,
                                        @CurrentMemberId Long memberId) {
        reservationService.postReview(reservationId, reqDto, memberId);
        return ResponseEntity.status(201).build();
    }
}
