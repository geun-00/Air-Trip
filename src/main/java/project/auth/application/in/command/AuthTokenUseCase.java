package project.auth.application.in.command;

import project.auth.application.in.command.model.AuthTokenResult;
import project.auth.application.in.command.model.LoginCommand;
import project.auth.application.in.command.model.LogoutCommand;
import project.auth.application.in.command.model.RefreshAccessTokenCommand;

public interface AuthTokenUseCase {

    AuthTokenResult login(LoginCommand command);

    AuthTokenResult refreshAccessToken(RefreshAccessTokenCommand command);

    void logout(LogoutCommand command);
}
