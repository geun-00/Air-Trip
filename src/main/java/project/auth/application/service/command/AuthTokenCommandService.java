package project.auth.application.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import project.auth.application.event.OAuthLogoutEvent;
import project.auth.application.in.command.IssueAuthTokenUseCase;
import project.auth.application.in.command.LogoutUseCase;
import project.auth.application.in.command.RefreshAccessTokenUseCase;
import project.auth.application.in.command.model.AuthTokenResult;
import project.auth.application.in.command.model.IssueAuthTokenCommand;
import project.auth.application.in.command.model.LogoutCommand;
import project.auth.application.in.command.model.RefreshAccessTokenCommand;
import project.auth.application.out.command.AuthTokenPort;
import project.auth.application.out.command.ManageBlacklistedTokenPort;
import project.auth.application.out.command.ManageRefreshTokenPort;
import project.auth.application.out.command.model.IssuedAuthTokens;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.member.application.out.command.LoadMemberPort;
import project.member.domain.Member;

@Service
@RequiredArgsConstructor
public class AuthTokenCommandService implements IssueAuthTokenUseCase,
                                                RefreshAccessTokenUseCase,
                                                LogoutUseCase {

    private final AuthTokenPort authTokenPort;
    private final LoadMemberPort loadMemberPort;
    private final ApplicationEventPublisher eventPublisher;
    private final ManageRefreshTokenPort manageRefreshTokenPort;
    private final ManageBlacklistedTokenPort manageBlacklistedTokenPort;

    @Override
    public AuthTokenResult issue(IssueAuthTokenCommand command) {
        Member member = loadMemberPort.loadByEmail(command.email());
        return issueToken(member, command.principalName());
    }

    @Override
    public AuthTokenResult refreshAccessToken(RefreshAccessTokenCommand command) {
        String refreshToken = command.refreshToken();
        authTokenPort.validate(refreshToken);

        if (!manageRefreshTokenPort.exists(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        Long memberId = authTokenPort.loadMemberId(refreshToken);
        String principalName = authTokenPort.loadPrincipalName(refreshToken);
        manageRefreshTokenPort.delete(refreshToken);

        Member member = loadMemberPort.loadById(memberId);
        return issueToken(member, principalName);
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
        addBlackList(command.accessToken());

        Long memberId = authTokenPort.loadMemberId(command.refreshToken());
        manageRefreshTokenPort.delete(command.refreshToken());

        Member member = loadMemberPort.loadById(memberId);
        eventPublisher.publishEvent(new OAuthLogoutEvent(member.getSocialType()));
    }

    private void addBlackList(String accessToken) {
        long remainingMillis = authTokenPort.loadRemainingMillis(accessToken);
        if (remainingMillis > 0) {
            manageBlacklistedTokenPort.save(accessToken, remainingMillis);
        }
    }
}
