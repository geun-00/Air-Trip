package project.controller.members;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import project.common.adapter.in.web.response.PageResponse;
import project.controller.RestDocsTestSupport;
import project.member.adapter.in.web.MemberQueryController;
import project.member.adapter.in.web.response.ChatMemberSearchResponse;
import project.member.adapter.in.web.response.ChatMembersSearchResponse;
import project.member.adapter.in.web.response.DefaultProfileResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;
import project.member.application.in.query.GetMyProfileQueryUseCase;
import project.member.application.in.query.GetMyTripsHistoryQueryUseCase;
import project.member.application.in.query.SearchMembersByNameQueryUseCase;
import project.security.WithMockMember;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberQueryController.class)
class MemberQueryControllerTest extends RestDocsTestSupport {

    private static final String MEMBER_API_TAG = "Member API";

    @MockitoBean
    GetMyProfileQueryUseCase getMyProfileQueryUseCase;
    @MockitoBean
    SearchMembersByNameQueryUseCase searchMembersByNameQueryUseCase;
    @MockitoBean
    GetMyTripsHistoryQueryUseCase getMyTripsHistoryQueryUseCase;

    @Test
    @DisplayName("내 기본 정보 조회")
    @WithMockMember
    void getDefaultProfile() throws Exception {
        DefaultProfileResponse response = new DefaultProfileResponse("Antonio Cui", "https://example.com/a.jpg", LocalDate.of(2024, 8, 15), "Accumsan luctus fringilla cubilia tempor auctor ullamcorper.", true);
        given(getMyProfileQueryUseCase.getMyProfile(anyLong())).willReturn(response);

        mockMvc.perform(get("/api/members/me")
                       .header(AUTHORIZATION, "Bearer {access-token}")
               )
               .andExpectAll(
                       handler().handlerType(MemberQueryController.class),
                       handler().methodName("getMyProfile"),
                       status().isOk(),
                       jsonPath("$.name").value(response.name()),
                       jsonPath("$.profileImageUrl").value(response.profileImageUrl()),
                       jsonPath("$.createdDate").value(response.createdDate().toString()),
                       jsonPath("$.aboutMe").value(response.aboutMe()),
                       jsonPath("$.isEmailVerified").value(response.isEmailVerified())
               )
               .andDo(document("get-my-profile",
                       resource(
                               builder()
                                       .tag(MEMBER_API_TAG)
                                       .summary("내 프로필 정보 조회")
                                       .requestHeaders(headerWithName(AUTHORIZATION).description("Bearer {액세스 토큰}"))
                                       .responseFields(
                                               fieldWithPath("name")
                                                       .type(STRING)
                                                       .description("이름"),
                                               fieldWithPath("profileImageUrl")
                                                       .type(STRING)
                                                       .optional()
                                                       .description("프로필 이미지 URL"),
                                               fieldWithPath("createdDate")
                                                       .type(STRING)
                                                       .description("가입날짜"),
                                               fieldWithPath("aboutMe")
                                                       .type(STRING)
                                                       .optional()
                                                       .description("자기소개글"),
                                               fieldWithPath("isEmailVerified")
                                                       .type(BOOLEAN)
                                                       .description("이메일 인증 완료 여부")
                                       )
                                       .responseSchema(schema("DefaultProfileResponse"))
                                       .build()
                       )
               ));
    }

    @Test
    @DisplayName("이름으로 사용자 조회")
    void findMembersByName() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        List<ChatMemberSearchResponse> memberSearchDtos = List.of(
                new ChatMemberSearchResponse(1L, "kim-1", now.minusDays(5), "https://example-a.com"),
                new ChatMemberSearchResponse(2L, "kim-2", now.minusDays(6), "https://example-b.com"),
                new ChatMemberSearchResponse(3L, "kim-3", now.minusDays(7), "https://example-c.com")
        );
        ChatMembersSearchResponse response = new ChatMembersSearchResponse(memberSearchDtos);
        given(searchMembersByNameQueryUseCase.findMembersByName(anyString())).willReturn(response);

        mockMvc.perform(get("/api/members/search")
                       .param("name", "kim"))
               .andExpectAll(
                       handler().handlerType(MemberQueryController.class),
                       handler().methodName("findMembersByName"),
                       status().isOk(),
                       jsonPath("$.members.length()").value(memberSearchDtos.size())
               )
               .andDo(document("find-member-by-name",
                       resource(
                               builder()
                                       .tag(MEMBER_API_TAG)
                                       .summary("이름으로 사용자 조회")
                                       .description("name 파라미터 값이 포함된 모든 사용자를 응답합니다.")
                                       .queryParameters(parameterWithName("name").description("검색 이름"))
                                       .responseFields(
                                               fieldWithPath("members[].id")
                                                       .type(NUMBER)
                                                       .description("ID"),
                                               fieldWithPath("members[].name")
                                                       .type(STRING)
                                                       .description("이름"),
                                               fieldWithPath("members[].createdDateTime")
                                                       .type(STRING)
                                                       .description("가입일"),
                                               fieldWithPath("members[].profileImageUrl")
                                                       .type(STRING)
                                                       .optional()
                                                       .description("프로필 이미지 URL")
                                       )
                                       .responseSchema(schema("MemberSearchResponse"))
                                       .build()
                       ))
               );
    }

    @Test
    @DisplayName("여행한 숙소 목록 조회")
    @WithMockMember
    void getTripsHistory() throws Exception {
        LocalDate now = LocalDate.now();
        List<TripHistoryResponse> dtos = List.of(
                new TripHistoryResponse(1L, 1L, "https://example-a.com", "title-A", now.minusDays(14), now.minusDays(12), true),
                new TripHistoryResponse(2L, 2L, "https://example-b.com", "title-B", now.minusDays(10), now.minusDays(9), false),
                new TripHistoryResponse(3L, 3L, "https://example-c.com", "title-C", now.minusDays(7), now.minusDays(4), true)
        );
        PageResponse<TripHistoryResponse> response = PageResponse.<TripHistoryResponse>builder()
                                                                 .contents(dtos)
                                                                 .pageNumber(0)
                                                                 .pageSize(10)
                                                                 .total(dtos.size())
                                                                 .build();
        given(getMyTripsHistoryQueryUseCase.getTripsHistory(anyLong(), any()))
                .willReturn(response);

        mockMvc.perform(get("/api/members/me/trips/past")
                       .header(AUTHORIZATION, "Bearer {access-token}")
                       .param("page", "0")
                       .param("size", "10")
               )
               .andExpectAll(
                       handler().handlerType(MemberQueryController.class),
                       handler().methodName("getTripsHistory"),
                       status().isOk(),
                       jsonPath("$.contents.length()").value(dtos.size())
               )
               .andDo(document("get-trips-past",
                       resource(
                               builder()
                                       .tag(MEMBER_API_TAG)
                                       .summary("여행한 숙소 목록 조회")
                                       .requestHeaders(headerWithName(AUTHORIZATION).description("Bearer {액세스 토큰}"))
                                       .queryParameters(
                                               parameterWithName("size").optional().description("페이지 크기"),
                                               parameterWithName("page").optional().description("페이지 번호 (0-index)")
                                       )
                                       .responseFields(
                                               fieldWithPath("contents")
                                                       .type(ARRAY)
                                                       .description("검색 페이지 데이터"),
                                               fieldWithPath("hasPrev")
                                                       .type(BOOLEAN)
                                                       .description("이전 페이지 존재 여부"),
                                               fieldWithPath("hasNext")
                                                       .type(BOOLEAN)
                                                       .description("다음 페이지 존재 여부"),
                                               fieldWithPath("totalCount")
                                                       .type(NUMBER)
                                                       .description("검색된 전체 데이터 개수"),
                                               fieldWithPath("prevPage")
                                                       .type(NUMBER)
                                                       .description("이전 페이지 번호 (0-index, 없으면 -1)"),
                                               fieldWithPath("nextPage")
                                                       .type(NUMBER)
                                                       .description("다음 페이지 번호 (0-index, 없으면 -1)"),
                                               fieldWithPath("totalPage")
                                                       .type(NUMBER)
                                                       .description("총 페이지 개수"),
                                               fieldWithPath("current")
                                                       .type(NUMBER)
                                                       .description("현재 페이지 번호 (0-index)"),
                                               fieldWithPath("size")
                                                       .type(NUMBER)
                                                       .description("페이지 크기"),
                                               fieldWithPath("contents[].reservationId")
                                                       .type(NUMBER)
                                                       .description("예약 ID"),
                                               fieldWithPath("contents[].accommodationId")
                                                       .type(NUMBER)
                                                       .description("숙소 ID"),
                                               fieldWithPath("contents[].thumbnailUrl")
                                                       .type(STRING)
                                                       .description("숙소 썸네일 URL"),
                                               fieldWithPath("contents[].title")
                                                       .type(STRING)
                                                       .description("숙소 제목"),
                                               fieldWithPath("contents[].startDate")
                                                       .type(STRING)
                                                       .description("여행 시작일"),
                                               fieldWithPath("contents[].endDate")
                                                       .type(STRING)
                                                       .description("여행 종료일"),
                                               fieldWithPath("contents[].hasReviewed")
                                                       .type(BOOLEAN)
                                                       .description("리뷰 등록 여부")
                                       )
                                       .responseSchema(schema("TripHistoryResponse"))
                                       .build()
                       ))
               );
    }
}
