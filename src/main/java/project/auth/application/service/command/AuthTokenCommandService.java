package project.auth.application.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import project.auth.application.in.command.AuthTokenUseCase;
import project.auth.application.event.OAuthLogoutEvent;
import project.auth.application.in.command.IssueAuthTokenUseCase;
import project.auth.application.in.command.model.AuthTokenResult;
import project.auth.application.in.command.model.IssueAuthTokenCommand;
import project.auth.application.in.command.model.LoginCommand;
import project.auth.application.in.command.model.LogoutCommand;
import project.auth.application.in.command.model.RefreshAccessTokenCommand;
import project.auth.application.out.command.AuthTokenPort;
import project.auth.application.out.command.ManageBlacklistedTokenPort;
import project.auth.application.out.command.ManageRefreshTokenPort;
import project.auth.application.out.command.model.AuthTokenClaims;
import project.auth.application.out.command.model.IssuedAuthTokens;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.member.application.out.command.ReadMemberPort;
import project.member.domain.Member;
import project.member.domain.PasswordMatcher;

@Service
@RequiredArgsConstructor
public class AuthTokenCommandService implements IssueAuthTokenUseCase,
                                                AuthTokenUseCase {

    private static final String LOCAL_LOGIN_PRINCIPAL_NAME = "local";

    private final AuthTokenPort authTokenPort;
    private final ReadMemberPort readMemberPort;
    private final PasswordMatcher passwordMatcher;
    private final ApplicationEventPublisher eventPublisher;
    private final ManageRefreshTokenPort manageRefreshTokenPort;
    private final ManageBlacklistedTokenPort manageBlacklistedTokenPort;

    @Override
    public AuthTokenResult issue(IssueAuthTokenCommand command) {
        Member member = readMemberPort.getByEmail(command.email());
        return issueToken(member, command.principalName());
    }

    @Override
    public AuthTokenResult login(LoginCommand command) {
        Member member = loadMemberForLogin(command.email());
        member.validatePassword(command.password(), passwordMatcher);
        return issueToken(member, LOCAL_LOGIN_PRINCIPAL_NAME);
    }

    private Member loadMemberForLogin(String email) {
        try {
            return readMemberPort.getByEmail(email);
        } catch (BusinessException exception) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Override
    public AuthTokenResult refreshAccessToken(RefreshAccessTokenCommand command) {
        String refreshToken = command.refreshToken();
        authTokenPort.validate(refreshToken);

        if (!manageRefreshTokenPort.exists(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        AuthTokenClaims claims = authTokenPort.loadClaims(refreshToken);
        manageRefreshTokenPort.delete(refreshToken);

        Member member = readMemberPort.getById(claims.memberId());
        return issueToken(member, claims.principalName());
    }

    private AuthTokenResult issueToken(Member member, String principalName) {
        IssuedAuthTokens issuedTokens = authTokenPort.issue(member, principalName);
        manageRefreshTokenPort.save(issuedTokens.refreshToken(), member.getId(), issuedTokens.refreshTokenTtlSeconds());

        return new AuthTokenResult(
                issuedTokens.accessToken(),
                issuedTokens.refreshToken(),
                issuedTokens.accessTokenTtlSeconds(),
                issuedTokens.refreshTokenTtlSeconds()
        );
    }

    @Override
    public void logout(LogoutCommand command) {
        String refreshToken = command.refreshToken();
        authTokenPort.validate(refreshToken);

        if (!manageRefreshTokenPort.exists(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        addBlackList(command.accessToken());

        AuthTokenClaims claims = authTokenPort.loadClaims(refreshToken);
        manageRefreshTokenPort.delete(refreshToken);

        Member member = readMemberPort.getById(claims.memberId());
        eventPublisher.publishEvent(new OAuthLogoutEvent(member.getSocialType(), claims.principalName()));
    }

    private void addBlackList(String accessToken) {
        long remainingMillis = authTokenPort.loadRemainingMillis(accessToken);
        if (remainingMillis > 0) {
            manageBlacklistedTokenPort.save(accessToken, remainingMillis);
        }
    }
}
