package project.controller.members;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import project.controller.RestDocsTestSupport;
import project.member.adapter.in.web.EmailVerificationController;
import project.member.application.in.command.SendEmailVerificationUseCase;
import project.member.application.in.command.VerifyEmailUseCase;
import project.security.WithMockMember;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailVerificationController.class)
class EmailVerificationControllerTest extends RestDocsTestSupport {

    private static final String MEMBER_API_TAG = "Member API";

    @MockitoBean VerifyEmailUseCase verifyEmailUseCase;
    @MockitoBean SendEmailVerificationUseCase sendEmailVerificationUseCase;

    @Test
    @DisplayName("이메일 인증 요청")
    @WithMockMember
    void sendEmail() throws Exception {
        mockMvc.perform(post("/api/members/me/email-verification")
                       .header(AUTHORIZATION, "Bearer {access-token}")
               )
               .andExpectAll(
                       handler().handlerType(EmailVerificationController.class),
                       handler().methodName("sendEmail"),
                       status().isOk()
               )
               .andDo(document("send-email",
                       resource(
                               builder()
                                       .tag(MEMBER_API_TAG)
                                       .summary("이메일 인증 요청")
                                       .description("회원가입 과정에서 저장된 이메일로 인증을 진행합니다.")
                                       .requestHeaders(headerWithName(AUTHORIZATION).description("Bearer {액세스 토큰}"))
                                       .build()
                       )
               ));
    }

    @Test
    @DisplayName("이메일 인증 토큰 검증")
    void verifyEmail() throws Exception {
        String redirectUrl = "http://localhost:3000/users/profile?emailVerify=success";
        given(verifyEmailUseCase.verifyToken(anyString())).willReturn(redirectUrl);

        mockMvc.perform(get("/api/members/email-verification")
                       .queryParam("token", "email-verification-token")
               )
               .andExpectAll(
                       handler().handlerType(EmailVerificationController.class),
                       handler().methodName("verifyEmail"),
                       status().isFound(),
                       header().string(LOCATION, redirectUrl)
               )
               .andDo(document("verify-email",
                       queryParameters(parameterWithName("token").description("이메일 인증 토큰")),
                       resource(
                               builder()
                                       .tag(MEMBER_API_TAG)
                                       .summary("이메일 인증 토큰 검증")
                                       .responseHeaders(headerWithName(LOCATION).description("인증 결과 리다이렉트 URL"))
                                       .build()
                       )
               ));
    }
}
