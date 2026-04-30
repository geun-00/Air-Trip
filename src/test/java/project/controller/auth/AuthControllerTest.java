package project.controller.auth;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import project.auth.adapter.in.web.AuthController;
import project.auth.application.in.command.LogoutUseCase;
import project.auth.application.in.command.RefreshAccessTokenUseCase;
import project.auth.application.in.command.model.AuthTokenResult;
import project.auth.application.in.command.model.RefreshAccessTokenCommand;
import project.controller.RestDocsTestSupport;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends RestDocsTestSupport {

    public static final String AUTH_API_TAG = "Auth API";

    @MockitoBean LogoutUseCase logoutUseCase;
    @MockitoBean RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    @Test
    @DisplayName("쿠키로 받은 리프레시 토큰으로 액세스 토큰을 갱신한다.")
    void refreshAccessToken() throws Exception {
        //given
        given(refreshAccessTokenUseCase.refreshAccessToken(any(RefreshAccessTokenCommand.class)))
                .willReturn(new AuthTokenResult("dummy-access-token", "dummy-refresh-token", 1800, 604800));

        //when
        //then
        mockMvc.perform(post("/api/auth/refresh")
                       .cookie(new Cookie("RefreshToken", "refresh-token"))
               )
               .andExpectAll(
                       handler().handlerType(AuthController.class),
                       handler().methodName("refreshAccessToken"),
                       status().isOk()
               )
               .andDo(document("refresh-accessToken",
                       requestCookies(cookieWithName("RefreshToken").description("로그인 시 전달된 리프레시 토큰")),
                       resource(
                               builder()
                                       .tag(AUTH_API_TAG)
                                       .summary("액세스 토큰 갱신")
                                       .description("Key=RefreshToken 쿠키로 RefreshToken을 전달해주세요.")
                                       .responseHeaders(
                                               headerWithName(AUTHORIZATION).description("새로운 액세스 토큰 발급"),
                                               headerWithName(SET_COOKIE).description("새로운 리프레시 토큰 발급")
                                       )
                                       .build()

                       )));
    }

    @Test
    @DisplayName("액세스 토큰과 리프레시 토큰을 받아 로그아웃 처리를 한다.")
    void logout() throws Exception {
        //given

        //when
        //then
        mockMvc.perform(post("/api/auth/logout")
                       .cookie(new Cookie("RefreshToken", "{refresh-token}"))
                       .header(AUTHORIZATION, "Bearer {access-token}")
               )
               .andExpectAll(
                       handler().handlerType(AuthController.class),
                       handler().methodName("logout"),
                       status().isOk()
               )
               .andDo(document("logout",
                       requestCookies(cookieWithName("RefreshToken").description("로그인 시 전달된 리프레시 토큰")),
                       resource(
                               builder()
                                       .tag(AUTH_API_TAG)
                                       .summary("로그아웃")
                                       .description("Key=RefreshToken 쿠키로 RefreshToken을 전달해주세요.")
                                       .requestHeaders(headerWithName(AUTHORIZATION).description("로그인 시 전달된 액세스 토큰"))
                                       .responseHeaders(headerWithName(SET_COOKIE).description("무효 처리된 리프레시 토큰"))
                                       .build()
                       )));
    }

}
