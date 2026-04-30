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
import project.auth.adapter.out.jwt.TokenService;

import static project.auth.adapter.out.jwt.JwtProperties.AUTHORIZATION_HEADER;
import static project.auth.adapter.out.jwt.JwtProperties.REFRESH_TOKEN_KEY;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;

    @PostMapping("/refresh")
    public void refreshAccessToken(@CookieValue(REFRESH_TOKEN_KEY) String refreshToken,
                                   HttpServletResponse response) {
        tokenService.refreshAccessToken(refreshToken, response);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                       @CookieValue(REFRESH_TOKEN_KEY) String refreshToken,
                       HttpServletResponse response) {
        tokenService.logoutProcess(accessToken, refreshToken);

        // Access Token 쿠키 삭제
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                                                         .path("/")
                                                         .httpOnly(true)
                                                         .maxAge(0)
                                                         .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        // Refresh Token 쿠키 삭제
        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_KEY, "")
                                                          .path("/")
                                                          .httpOnly(true)
                                                          .maxAge(0)
                                                          .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }
}
