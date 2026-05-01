package project.controller.members;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import project.common.adapter.in.web.response.PageResponse;
import project.controller.RestDocsTestSupport;
import project.member.adapter.in.web.MemberQueryController;
import project.member.adapter.in.web.response.DefaultProfileResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;
import project.member.application.in.query.ReadMemberProfileUseCase;
import project.member.application.in.query.ReadViewedAccommodationsUseCase;
import project.member.application.in.query.SearchMembersUseCase;
import project.member.application.in.query.model.ChatMemberSearchView;
import project.member.application.in.query.model.ChatMembersSearchView;
import project.member.application.in.query.model.DefaultProfileView;
import project.member.application.in.query.model.TripHistoryView;
import project.member.application.in.query.model.ViewHistoryAccommodationView;
import project.member.application.in.query.model.ViewHistoryGroupView;
import project.security.WithMockMember;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.hamcrest.Matchers.hasSize;
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
    ReadMemberProfileUseCase readMemberProfileUseCase;
    @MockitoBean
    SearchMembersUseCase searchMembersUseCase;
    @MockitoBean
    ReadViewedAccommodationsUseCase readViewedAccommodationsUseCase;

    @Test
    @DisplayName("내 기본 정보 조회")
    @WithMockMember
    void getDefaultProfile() throws Exception {
        DefaultProfileView profile = new DefaultProfileView("Antonio Cui", "https://example.com/a.jpg", LocalDate.of(2024, 8, 15), "Accumsan luctus fringilla cubilia tempor auctor ullamcorper.", true);
        DefaultProfileResponse response = new DefaultProfileResponse(
                profile.name(),
                profile.profileImageUrl(),
                profile.createdDate(),
                profile.aboutMe(),
                profile.isEmailVerified()
        );
        given(readMemberProfileUseCase.getMyProfile(anyLong())).willReturn(profile);

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
        List<ChatMemberSearchView> members = List.of(
                new ChatMemberSearchView(1L, "kim-1", now.minusDays(5), "https://example-a.com"),
                new ChatMemberSearchView(2L, "kim-2", now.minusDays(6), "https://example-b.com"),
                new ChatMemberSearchView(3L, "kim-3", now.minusDays(7), "https://example-c.com")
        );
        given(searchMembersUseCase.findMembersByName(anyString())).willReturn(new ChatMembersSearchView(members));

        mockMvc.perform(get("/api/members/search")
                       .param("name", "kim"))
               .andExpectAll(
                       handler().handlerType(MemberQueryController.class),
                       handler().methodName("findMembersByName"),
                       status().isOk(),
                       jsonPath("$.members.length()").value(members.size())
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
        List<TripHistoryView> tripHistories = List.of(
                new TripHistoryView(1L, 1L, "https://example-a.com", "title-A", now.minusDays(14), now.minusDays(12), true),
                new TripHistoryView(2L, 2L, "https://example-b.com", "title-B", now.minusDays(10), now.minusDays(9), false),
                new TripHistoryView(3L, 3L, "https://example-c.com", "title-C", now.minusDays(7), now.minusDays(4), true)
        );
        List<TripHistoryResponse> dtos = List.of(
                new TripHistoryResponse(1L, 1L, "https://example-a.com", "title-A", now.minusDays(14), now.minusDays(12), true),
                new TripHistoryResponse(2L, 2L, "https://example-b.com", "title-B", now.minusDays(10), now.minusDays(9), false),
                new TripHistoryResponse(3L, 3L, "https://example-c.com", "title-C", now.minusDays(7), now.minusDays(4), true)
        );
        PageResponse<TripHistoryView> response = PageResponse.from(new PageImpl<>(
                tripHistories,
                PageRequest.of(0, 10),
                dtos.size()
        ));
        given(readMemberProfileUseCase.getTripsHistory(anyLong(), any())).willReturn(response);

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

    @Test
    @DisplayName("최근 조회 숙소 이력")
    @WithMockMember
    void getRecentViewAccommodations() throws Exception {
        //given
        LocalDateTime today = LocalDateTime.now();
        List<ViewHistoryAccommodationView> todays = List.of(new ViewHistoryAccommodationView(today.minusHours(1), 1L, "호텔A", 4.5, "https://example.com/a.jpg", true, 1L, "my-wishlist-1"), new ViewHistoryAccommodationView(today.minusHours(2), 2L, "호텔B", 4.8, "https://example.com/b.jpg", false, null, null), new ViewHistoryAccommodationView(today.minusHours(3), 3L, "호텔C", 4.3, "https://example.com/c.jpg", true, 3L, "my-wishlist-3"));

        LocalDateTime yesterday = LocalDateTime.now()
                                               .minusDays(1);
        List<ViewHistoryAccommodationView> yesterdays = List.of(new ViewHistoryAccommodationView(yesterday.minusHours(1), 4L, "호텔D", 4.0, "https://example.com/d.jpg", false, null, null), new ViewHistoryAccommodationView(yesterday.minusHours(2), 5L, "호텔E", 3.9, "https://example.com/e.jpg", true, 5L, "my-wishlist-5"));

        List<ViewHistoryGroupView> result = List.of(new ViewHistoryGroupView(today.toLocalDate(), todays), new ViewHistoryGroupView(yesterday.toLocalDate(), yesterdays));
        given(readViewedAccommodationsUseCase.getRecentViewAccommodations(any())).willReturn(result);

        //when
        //then
        mockMvc.perform(get("/api/members/me/history/accommodations").header(AUTHORIZATION, "Bearer access-token"))
               .andExpectAll(
                       handler().handlerType(MemberQueryController.class),
                       handler().methodName("getRecentViewAccommodations"),
                       status().isOk(),
                       jsonPath("$.length()").value(result.size()),
                       jsonPath("$[0].accommodations", hasSize(result.get(0).accommodations().size())),
                       jsonPath("$[1].accommodations", hasSize(result.get(1).accommodations().size()))
               )
               .andDo(document(
                       "recent-view-accommodations",
                       resource(builder().tag(MEMBER_API_TAG)
                                         .summary("최근 조회한 숙소 목록")
                                         .description("최근 30일 숙소 조회 이력을 응답합니다.")
                                         .requestHeaders(headerWithName(AUTHORIZATION).description("Bearer {액세스 토큰}"))
                                         .responseFields(
                                                 fieldWithPath("[].date")
                                                         .type(STRING)
                                                         .description("조회일 (내림차순)"),
                                                 fieldWithPath("[].accommodations")
                                                         .type(ARRAY)
                                                         .description("숙소 목록 (시간 내림차순)"),
                                                 fieldWithPath("[].accommodations[].viewDate")
                                                         .type(STRING)
                                                         .description("조회일(시간 포함)"),
                                                 fieldWithPath("[].accommodations[].accommodationId")
                                                         .type(NUMBER)
                                                         .description("숙소 ID"),
                                                 fieldWithPath("[].accommodations[].title")
                                                         .type(STRING)
                                                         .description("숙소 이름"),
                                                 fieldWithPath("[].accommodations[].avgRate")
                                                         .type(NUMBER)
                                                         .description("평균 평점"),
                                                 fieldWithPath("[].accommodations[].thumbnailUrl")
                                                         .type(STRING)
                                                         .description("썸네일 URL"),
                                                 fieldWithPath("[].accommodations[].isInWishlist")
                                                         .type(BOOLEAN)
                                                         .description("위시리스트에 저장된 숙소인지 여부"),
                                                 fieldWithPath("[].accommodations[].wishlistId")
                                                         .type(NUMBER)
                                                         .optional()
                                                         .description("저장된 위시리스트 ID (isInWishlist = true일 때만, false면 null)"),
                                                 fieldWithPath("[].accommodations[].wishlistName")
                                                         .type(STRING)
                                                         .optional()
                                                         .description("저장된 위시리스트 이름 (isInWishlist = true일 때만, false면 null)")
                                         )
                                         .responseSchema(schema("RecentViewAccommodationsResponse"))
                                         .build())
               ));
    }
}
