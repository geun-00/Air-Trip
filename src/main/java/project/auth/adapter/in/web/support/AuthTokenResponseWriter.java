package project.auth.adapter.in.web.support;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import project.auth.application.in.command.model.AuthTokenResult;

import java.time.Duration;

import static project.infrastructure.jwt.JwtProperties.ACCESS_TOKEN_KEY;
import static project.infrastructure.jwt.JwtProperties.AUTHORIZATION_HEADER;
import static project.infrastructure.jwt.JwtProperties.REFRESH_TOKEN_KEY;
import static project.infrastructure.jwt.JwtProperties.TOKEN_PREFIX;

@Component
public class AuthTokenResponseWriter {

    public void write(HttpServletResponse response, AuthTokenResult result) {
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

    public void expire(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, expireCookie(ACCESS_TOKEN_KEY).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expireCookie(REFRESH_TOKEN_KEY).toString());
    }

    private ResponseCookie expireCookie(String name) {
        return ResponseCookie.from(name, "")
                             .path("/")
                             .httpOnly(true)
                             .maxAge(0)
                             .build();
    }
}
