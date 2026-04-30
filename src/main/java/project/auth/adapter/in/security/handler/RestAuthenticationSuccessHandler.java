package project.auth.adapter.in.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import project.auth.adapter.in.web.support.AuthTokenResponseWriter;
import project.auth.adapter.out.oauth.model.PrincipalUser;
import project.auth.application.in.command.IssueAuthTokenUseCase;
import project.auth.application.in.command.model.AuthTokenResult;
import project.auth.application.in.command.model.IssueAuthTokenCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final IssueAuthTokenUseCase issueAuthTokenUseCase;
    private final AuthTokenResponseWriter authTokenResponseWriter;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        PrincipalUser principal = (PrincipalUser) authentication.getPrincipal();

        String email = principal.providerUser().getEmail();
        AuthTokenResult result = issueAuthTokenUseCase.issue(new IssueAuthTokenCommand(email, "default"));
        authTokenResponseWriter.write(response, result);

        log.debug("REST 인증 성공, 토큰 발급");
    }
}
