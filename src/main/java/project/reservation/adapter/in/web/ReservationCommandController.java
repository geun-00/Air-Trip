package project.reservation.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.reservation.adapter.in.web.request.CreateReservationRequest;
import project.reservation.adapter.in.web.response.CreateReservationResponse;
import project.reservation.application.in.command.CreateReservationUseCase;
import project.reservation.application.in.command.model.CreateReservationCommand;
import project.reservation.application.in.command.model.CreateReservationResult;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationCommandController {

    private final CreateReservationUseCase createReservationUseCase;

    @PostMapping("/{accommodationId}")
    public ResponseEntity<CreateReservationResponse> postReservation(
            @CurrentMemberId Long memberId,
            @PathVariable Long accommodationId,
            @RequestBody CreateReservationRequest request
    ) {
        CreateReservationResult result = createReservationUseCase.createReservation(
                new CreateReservationCommand(
                        memberId,
                        accommodationId,
                        request.startDate(),
                        request.endDate(),
                        request.adults(),
                        request.children(),
                        request.infants()
                ));

        return ResponseEntity.ok(toResponse(result));
    }

    private CreateReservationResponse toResponse(CreateReservationResult result) {
        return new CreateReservationResponse(
                result.reservationId(),
                result.thumbnailUrl(),
                result.title(),
                result.refundRegulation(),
                result.startDate(),
                result.endDate(),
                result.adults(),
                result.children(),
                result.infants()
        );
    }
}
