package project.auth.adapter.in.web;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.auth.application.in.command.LogoutUseCase;
import project.auth.application.in.command.RefreshAccessTokenUseCase;
import project.auth.application.in.command.model.AuthTokenResult;
import project.auth.application.in.command.model.LogoutCommand;
import project.auth.application.in.command.model.RefreshAccessTokenCommand;

import java.time.Duration;

import static project.infrastructure.jwt.JwtProperties.ACCESS_TOKEN_KEY;
import static project.infrastructure.jwt.JwtProperties.AUTHORIZATION_HEADER;
import static project.infrastructure.jwt.JwtProperties.REFRESH_TOKEN_KEY;
import static project.infrastructure.jwt.JwtProperties.TOKEN_PREFIX;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LogoutUseCase logoutUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    @PostMapping("/refresh")
    public void refreshAccessToken(
            @CookieValue(REFRESH_TOKEN_KEY) String refreshToken,
            HttpServletResponse response
    ) {
        AuthTokenResult result = refreshAccessTokenUseCase.refreshAccessToken(new RefreshAccessTokenCommand(refreshToken));

        response.addHeader(AUTHORIZATION_HEADER, TOKEN_PREFIX + result.accessToken());
        response.addHeader(HttpHeaders.SET_COOKIE, createAccessTokenCookie(result).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(result).toString());
    }

    private ResponseCookie createAccessTokenCookie(AuthTokenResult result) {
        return ResponseCookie.from(ACCESS_TOKEN_KEY, result.accessToken())
                             .path("/")
                             .secure(true)
                             .sameSite("None")
                             .httpOnly(true)
                             .maxAge(Duration.ofSeconds(result.accessTokenTtlSeconds()))
                             .build();
    }

    private ResponseCookie createRefreshTokenCookie(AuthTokenResult result) {
        return ResponseCookie.from(REFRESH_TOKEN_KEY, result.refreshToken())
                             .path("/")
                             .secure(true)
                             .sameSite("None")
                             .httpOnly(true)
                             .maxAge(Duration.ofSeconds(result.refreshTokenTtlSeconds()))
                             .build();
    }

    @PostMapping("/logout")
    public void logout(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
            @CookieValue(REFRESH_TOKEN_KEY) String refreshToken,
            HttpServletResponse response
    ) {
        logoutUseCase.logout(new LogoutCommand(resolveAccessToken(accessToken), refreshToken));

        response.addHeader(HttpHeaders.SET_COOKIE, expireCookie(ACCESS_TOKEN_KEY).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expireCookie(REFRESH_TOKEN_KEY).toString());
    }

    private String resolveAccessToken(String accessToken) {
        if (accessToken.startsWith(TOKEN_PREFIX)) {
            return accessToken.substring(TOKEN_PREFIX.length());
        }
        return accessToken;
    }

    private ResponseCookie expireCookie(String name) {
        return ResponseCookie.from(name, "")
                             .path("/")
                             .httpOnly(true)
                             .maxAge(0)
                             .build();
    }
}
