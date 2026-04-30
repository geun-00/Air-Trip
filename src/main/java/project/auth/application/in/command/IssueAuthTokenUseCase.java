package project.auth.application.in.command;

import project.auth.application.in.command.model.AuthTokenResult;
import project.auth.application.in.command.model.IssueAuthTokenCommand;

public interface IssueAuthTokenUseCase {

    AuthTokenResult issue(IssueAuthTokenCommand command);
}
