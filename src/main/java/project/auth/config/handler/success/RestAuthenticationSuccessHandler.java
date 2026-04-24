package project.auth.config.handler.success;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import project.auth.adapter.out.oauth.model.PrincipalUser;
import project.auth.adapter.out.jwt.TokenService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalUser principal = (PrincipalUser) authentication.getPrincipal();

        String email = principal.providerUser().getEmail();
        tokenService.generateAndSendToken(email, "default", response);

        log.debug("REST 인증 성공, 토큰 발급");
    }
}
