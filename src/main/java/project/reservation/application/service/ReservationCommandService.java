package project.reservation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.application.out.query.LoadAccommodationPort;
import project.accommodation.domain.Accommodation;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.member.application.out.command.LoadMemberPort;
import project.member.domain.Member;
import project.reservation.application.in.command.CreateReservationUseCase;
import project.reservation.application.in.command.model.CreateReservationCommand;
import project.reservation.application.in.command.model.CreateReservationResult;
import project.reservation.application.out.command.model.SaveReservationCommand;
import project.reservation.application.out.command.SaveReservationPort;
import project.reservation.application.out.query.LoadReservationPort;
import project.reservation.domain.Reservation;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationCommandService implements CreateReservationUseCase {

    private final LoadMemberPort loadMemberPort;
    private final SaveReservationPort saveReservationPort;
    private final LoadReservationPort loadReservationPort;
    private final LoadAccommodationPort loadAccommodationPort;

    @Override
    public CreateReservationResult createReservation(CreateReservationCommand command) {
        Member member = loadMemberPort.loadById(command.memberId());
        member.validateEmailVerified();

        Accommodation accommodation = loadAccommodationPort.loadAccommodation(command.accommodationId());

        LocalDateTime startDate = command.startDate().atStartOfDay();
        LocalDateTime endDate = command.endDate().plusDays(1).atStartOfDay();

        validateReservationAvailability(accommodation.getId(), startDate, endDate);

        Reservation reservation = saveReservationPort.saveReservation(
                new SaveReservationCommand(
                        member.getId(),
                        accommodation.getId(),
                        startDate,
                        endDate,
                        command.adults(),
                        command.children(),
                        command.infants()
                )
        );

        return toResult(reservation, accommodation);
    }

    private void validateReservationAvailability(
            Long accommodationId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        if (loadReservationPort.existsConfirmedReservation(accommodationId, startDate, endDate)) {
            throw new BusinessException(ErrorCode.ALREADY_RESERVED);
        }
    }

    private CreateReservationResult toResult(Reservation reservation, Accommodation accommodation) {
        String thumbnailUrl = loadAccommodationPort.loadThumbnailUrl(accommodation.getId());

        return new CreateReservationResult(
                reservation.getId(),
                thumbnailUrl,
                accommodation.getTitle(),
                accommodation.getRefundRegulation(),
                reservation.getStartDate(),
                reservation.getDisplayEndDate(),
                reservation.getAdults(),
                reservation.getChildren(),
                reservation.getInfants()
        );
    }
}
