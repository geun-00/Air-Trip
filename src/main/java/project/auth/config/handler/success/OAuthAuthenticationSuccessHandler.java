package project.auth.config.handler.success;

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
import project.common.exception.BusinessException;
import project.auth.domain.response.TokenResponse;
import project.auth.adapter.out.oauth.model.PrincipalUser;
import project.auth.adapter.out.oauth.model.ProviderUser;
import project.auth.adapter.out.jwt.TokenService;
import project.member.application.in.command.RegisterSocialMemberUseCase;
import project.member.application.in.command.model.RegisterSocialMemberCommand;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frondEndUrl;

    private final TokenService tokenService;
    private final RegisterSocialMemberUseCase registerSocialMemberUseCase;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            PrincipalUser principal = (PrincipalUser) authentication.getPrincipal();
            ProviderUser providerUser = principal.providerUser();

            registerSocialMemberUseCase.registerSocial(new RegisterSocialMemberCommand(
                    providerUser.getUsername(),
                    providerUser.getEmail(),
                    providerUser.getProvider(),
                    providerUser.getPassword(),
                    providerUser.getNumber(),
                    providerUser.getBirthDate(),
                    providerUser.getImageUrl()
            ));
            TokenResponse tokenResponse = tokenService.generateAndSendToken(providerUser.getEmail(), providerUser.getPrincipalName(), response);

            log.debug("OAuth 인증 성공, 토큰 발급");

            redirectStrategy.sendRedirect(request, response, frondEndUrl + "/auth/callback?token=" + tokenResponse.accessToken());

        } catch (BusinessException ex) {
            OAuth2Error oAuth2Error = new OAuth2Error(ex.getMessage(), "Email Already Exists", null);
            throw new OAuth2AuthenticationException(oAuth2Error, ex);
        }
    }
}
