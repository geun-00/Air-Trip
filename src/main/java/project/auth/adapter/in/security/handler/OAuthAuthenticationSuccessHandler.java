package project.auth.adapter.in.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import project.auth.adapter.in.web.support.AuthTokenResponseWriter;
import project.auth.adapter.out.oauth.model.OAuthPrincipal;
import project.auth.adapter.out.oauth.model.ProviderUser;
import project.auth.application.in.command.IssueAuthTokenUseCase;
import project.auth.application.in.command.model.AuthTokenResult;
import project.auth.application.in.command.model.IssueAuthTokenCommand;
import project.common.exception.BusinessException;
import project.member.application.in.command.RegisterSocialMemberUseCase;
import project.member.application.in.command.model.RegisterSocialMemberCommand;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    private final IssueAuthTokenUseCase issueAuthTokenUseCase;
    private final AuthTokenResponseWriter authTokenResponseWriter;
    private final RegisterSocialMemberUseCase registerSocialMemberUseCase;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            OAuthPrincipal principal = (OAuthPrincipal) authentication.getPrincipal();
            ProviderUser providerUser = principal.providerUser();

            registerSocialMemberUseCase.registerSocial(new RegisterSocialMemberCommand(
                    providerUser.getUsername(),
                    providerUser.getEmail(),
                    providerUser.getProvider(),
                    UUID.randomUUID().toString(),
                    providerUser.getNumber(),
                    providerUser.getBirthDate(),
                    providerUser.getImageUrl()
            ));
            AuthTokenResult tokenResponse = issueAuthTokenUseCase.issue(new IssueAuthTokenCommand(
                    providerUser.getEmail(),
                    providerUser.getPrincipalName()
            ));
            authTokenResponseWriter.write(response, tokenResponse);

            log.debug("OAuth 인증 성공, 토큰 발급");

            redirectStrategy.sendRedirect(request, response, frontendUrl + "/auth/callback?token=" + tokenResponse.accessToken());

        } catch (BusinessException ex) {
            OAuth2Error oAuth2Error = new OAuth2Error(ex.getMessage(), "Email Already Exists", null);
            throw new OAuth2AuthenticationException(oAuth2Error, ex);
        }
    }
}
