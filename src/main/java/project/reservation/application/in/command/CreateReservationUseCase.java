package project.reservation.application.in.command;

import project.reservation.application.in.command.model.CreateReservationCommand;
import project.reservation.application.in.command.model.CreateReservationResult;

public interface CreateReservationUseCase {

    CreateReservationResult createReservation(CreateReservationCommand command);
}
