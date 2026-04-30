package project.auth.adapter.in.web;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.request.LoginRequest;
import project.auth.adapter.in.web.support.AuthTokenResponseWriter;
import project.auth.application.in.command.AuthTokenUseCase;
import project.auth.application.in.command.model.AuthTokenResult;
import project.auth.application.in.command.model.LoginCommand;
import project.auth.application.in.command.model.LogoutCommand;
import project.auth.application.in.command.model.RefreshAccessTokenCommand;

import static project.infrastructure.jwt.JwtProperties.AUTHORIZATION_HEADER;
import static project.infrastructure.jwt.JwtProperties.REFRESH_TOKEN_KEY;
import static project.infrastructure.jwt.JwtProperties.TOKEN_PREFIX;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthTokenUseCase authTokenUseCase;
    private final AuthTokenResponseWriter authTokenResponseWriter;

    @PostMapping("/login")
    public void login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthTokenResult result = authTokenUseCase.login(new LoginCommand(request.email(), request.password()));
        authTokenResponseWriter.write(response, result);
    }

    @PostMapping("/refresh")
    public void refreshAccessToken(
            @CookieValue(REFRESH_TOKEN_KEY) String refreshToken,
            HttpServletResponse response
    ) {
        AuthTokenResult result = authTokenUseCase.refreshAccessToken(new RefreshAccessTokenCommand(refreshToken));
        authTokenResponseWriter.write(response, result);
    }

    @PostMapping("/logout")
    public void logout(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
            @CookieValue(REFRESH_TOKEN_KEY) String refreshToken,
            HttpServletResponse response
    ) {
        authTokenUseCase.logout(new LogoutCommand(resolveAccessToken(accessToken), refreshToken));
        authTokenResponseWriter.expire(response);
    }

    private String resolveAccessToken(String accessToken) {
        if (accessToken.startsWith(TOKEN_PREFIX)) {
            return accessToken.substring(TOKEN_PREFIX.length());
        }
        return accessToken;
    }
}
