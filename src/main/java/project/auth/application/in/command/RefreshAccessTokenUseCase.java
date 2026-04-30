package project.auth.application.in.command;

import project.auth.application.in.command.model.AuthTokenResult;
import project.auth.application.in.command.model.RefreshAccessTokenCommand;

public interface RefreshAccessTokenUseCase {

    AuthTokenResult refreshAccessToken(RefreshAccessTokenCommand command);
}
