package project.controller.members;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import project.controller.RestDocsTestSupport;
import project.member.adapter.in.web.MemberCommandController;
import project.member.adapter.in.web.request.EditProfileRequest;
import project.member.adapter.in.web.request.RegisterMemberRequest;
import project.member.application.in.command.RegisterMemberUseCase;
import project.member.application.in.command.model.EditProfileResult;
import project.member.application.in.command.EditMyProfileUseCase;
import project.security.WithMockMember;

import java.time.LocalDate;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberCommandController.class)
class MemberCommandControllerTest extends RestDocsTestSupport {

    private static final String MEMBER_API_TAG = "Member API";

    @MockitoBean
    EditMyProfileUseCase editMyProfileUseCase;

    @MockitoBean
    RegisterMemberUseCase registerMemberUseCase;

    @Test
    @DisplayName("REST 회원 가입")
    void signup() throws Exception {
        //given
        RegisterMemberRequest request = new RegisterMemberRequest(
                "Chris Shu",
                "email@test.com",
                "01012345678",
                LocalDate.of(2000, 9, 14),
                "password12@"
        );

        //when
        //then
        mockMvc.perform(post("/api/members")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(creatJson(request)))
               .andExpectAll(
                       handler().handlerType(MemberCommandController.class),
                       handler().methodName("signup"),
                       status().isCreated()
               )
               .andDo(document(
                       "rest-signup",
                       resource(builder().tag(MEMBER_API_TAG)
                                         .summary("REST 회원 가입")
                                         .requestFields(
                                                 fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                                 fieldWithPath("email").type(JsonFieldType.STRING).description("이메일 (제약사항 : 이메일 형식 준수)"),
                                                 fieldWithPath("number").type(JsonFieldType.STRING).description("전화번호 (제약사항 : 하이픈(-) 제외)").optional(),
                                                 fieldWithPath("birthDate").type(JsonFieldType.STRING).description("생일 (제약사항 : 과거일)").optional(),
                                                 fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호 (제약사항 : 8~15자리, 특수문자 포함)")
                                         )
                                         .requestSchema(schema("RegisterMemberRequest"))
                                         .build())
               ));
    }

    @Test
    @DisplayName("내 기본 정보 수정")
    @WithMockMember
    void editMyProfile() throws Exception {
        EditProfileRequest reqDto = new EditProfileRequest("Antonio Cui", "Accumsan luctus fringilla cubilia tempor auctor ullamcorper.", true);

        MockMultipartFile imageFile = new MockMultipartFile("profileImage", "test-file.jpg", MediaType.IMAGE_JPEG_VALUE, "file-content".getBytes());
        MockMultipartFile editProfileRequest = new MockMultipartFile("editProfileRequest", "test-request", MediaType.APPLICATION_JSON_VALUE, creatJson(reqDto).getBytes());

        EditProfileResult result = new EditProfileResult("Antonio Cui", "https://example.com/a.jpg", "Accumsan luctus fringilla cubilia tempor auctor ullamcorper.");
        given(editMyProfileUseCase.editMyProfile(any())).willReturn(result);

        mockMvc.perform(multipart("/api/members/me")
                       .file(imageFile)
                       .file(editProfileRequest)
                       .accept(MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE)
                       .header(AUTHORIZATION, "Bearer {access-token}")
                       .with(request -> {
                           request.setMethod("PUT");
                           return request;
                       })
                       .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
               )
               .andExpectAll(
                       handler().handlerType(MemberCommandController.class),
                       handler().methodName("editMyProfile"),
                       status().isOk(),
                       jsonPath("$.name").value(result.name()),
                       jsonPath("$.profileImageUrl").value(result.profileImageUrl()),
                       jsonPath("$.aboutMe").value(result.aboutMe())
               )
               .andDo(document("edit-my-profile",
                       requestParts(
                               partWithName("profileImage").optional().description("새로운 프로필 이미지 파일"),
                               partWithName("editProfileRequest").description("새로운 프로필 정보(JSON)")
                       ),
                       requestPartFields("editProfileRequest",
                               fieldWithPath("name")
                                       .type(STRING)
                                       .description("새로 저장할 이름"),
                               fieldWithPath("aboutMe")
                                       .type(STRING)
                                       .description("새로 저장할 소개글"),
                               fieldWithPath("isProfileImageChanged")
                                       .type(BOOLEAN)
                                       .description("이미지 파일 변경 여부")
                       ),
                       resource(
                               builder()
                                       .tag(MEMBER_API_TAG)
                                       .summary("내 프로필 정보 수정")
                                       .requestHeaders(headerWithName(AUTHORIZATION).description("Bearer {액세스 토큰}"))
                                       .responseFields(
                                               fieldWithPath("name")
                                                       .type(STRING)
                                                       .description("새로 저장된 이름"),
                                               fieldWithPath("profileImageUrl")
                                                       .type(STRING)
                                                       .description("새로 저장된 프로필 이미지 URL"),
                                               fieldWithPath("aboutMe")
                                                       .type(STRING)
                                                       .description("새로 저장된 자기소개글")
                                       )
                                       .requestSchema(schema("EditProfileRequest"))
                                       .responseSchema(schema("EditProfileResponse"))
                                       .build()
                       )
               ));
    }
}
