package project.auth.application.in.command;

import project.auth.application.in.command.model.LogoutCommand;

public interface LogoutUseCase {

    void logout(LogoutCommand command);
}
