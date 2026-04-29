package project.reservation.application.out.command;

import project.reservation.application.out.command.model.SaveReservationCommand;
import project.reservation.domain.Reservation;

public interface SaveReservationPort {

    Reservation saveReservation(SaveReservationCommand command);
}
