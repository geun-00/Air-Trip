package project.controller.accommodation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import project.accommodation.adapter.in.web.AccommodationQueryController;
import project.accommodation.application.in.query.GetAccommodationDetailQueryUseCase;
import project.accommodation.application.in.query.GetAccommodationPriceQueryUseCase;
import project.accommodation.application.in.query.GetMainAccommodationsQueryUseCase;
import project.accommodation.application.in.query.GetRecentViewAccommodationsQueryUseCase;
import project.accommodation.application.in.query.SearchAccommodationsQueryUseCase;
import project.accommodation.application.in.query.model.AccommodationDetailView;
import project.accommodation.application.in.query.model.AccommodationPriceView;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.in.query.model.MainAccommodationItemView;
import project.accommodation.application.in.query.model.MainAccommodationView;
import project.accommodation.application.in.query.model.ViewHistoryAccommodationView;
import project.accommodation.application.in.query.model.ViewHistoryGroupView;
import project.common.adapter.in.web.response.PageResponse;
import project.controller.RestDocsTestSupport;
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
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccommodationQueryController.class)
class AccommodationQueryControllerTest extends RestDocsTestSupport {

    private static final String ACCOMMODATION_API_TAG = "Accommodation API";

    @MockitoBean
    GetMainAccommodationsQueryUseCase getMainAccommodationsQueryUseCase;

    @MockitoBean
    SearchAccommodationsQueryUseCase searchAccommodationsQueryUseCase;

    @MockitoBean
    GetAccommodationDetailQueryUseCase getAccommodationDetailQueryUseCase;

    @MockitoBean
    GetRecentViewAccommodationsQueryUseCase getRecentViewAccommodationsQueryUseCase;

    @MockitoBean
    GetAccommodationPriceQueryUseCase getAccommodationPriceQueryUseCase;

    @Test
    @DisplayName("메인 페이지 숙소 목록 조회")
    void getAccommodations() throws Exception {
        // given
        List<MainAccommodationItemView> seoulAcc = List.of(
                new MainAccommodationItemView(1L, "호텔A", 100000, 4.5, "https://example.com/a.jpg", true, "my-wishlist-1", 1L, "서울", "code-1"),
                new MainAccommodationItemView(2L, "호텔B", 200000, 3.8, "https://example.com/b.jpg", false, null, null, "서울", "code-1")
        );
        List<MainAccommodationItemView> gyeonggiAcc = List.of(
                new MainAccommodationItemView(3L, "호텔C", 150000, 4.3, "https://example.com/c.jpg", false, null, null, "경기도", "code-2"),
                new MainAccommodationItemView(4L, "호텔D", 250000, 4.7, "https://example.com/d.jpg", true, "my-wishlist-2", 2L, "경기도", "code-2"),
                new MainAccommodationItemView(5L, "호텔E", 300000, 3.3, "https://example.com/e.jpg", true, "my-wishlist-2", 2L, "경기도", "code-2")
        );

        List<MainAccommodationView> result = List.of(
                new MainAccommodationView("서울", "code-1", seoulAcc),
                new MainAccommodationView("경기도", "code-2", gyeonggiAcc)
        );

        given(getMainAccommodationsQueryUseCase.getAccommodations(any())).willReturn(result);

        //when
        //then
        mockMvc.perform(get("/api/accommodations"))
               .andExpectAll(
                       handler().handlerType(AccommodationQueryController.class),
                       handler().methodName("getAccommodations"),
                       status().isOk(),
                       jsonPath("$", hasSize(result.size())),
                       jsonPath("$[0].areaName").value(result.get(0).areaName()),
                       jsonPath("$[1].areaName").value(result.get(1).areaName()),
                       jsonPath("$[0].accommodations", hasSize(result.get(0).accommodations().size())),
                       jsonPath("$[1].accommodations", hasSize(result.get(1).accommodations().size())),
                       jsonPath("$[0].accommodations[0].reservationCount").doesNotExist(),
                       jsonPath("$[0].accommodations[0].areaCode").doesNotExist()
               )
               .andDo(document("get-accommodations",
                       resource(
                               builder()
                                       .tag(ACCOMMODATION_API_TAG)
                                       .summary("지역별 숙소 목록 조회 (메인 페이지용)")
                                       .requestHeaders(headerWithName(AUTHORIZATION).optional().description("Bearer {액세스 토큰}"))
                                       .responseFields(
                                               fieldWithPath("[].areaName")
                                                       .type(STRING)
                                                       .description("지역명"),
                                               fieldWithPath("[].areaCode")
                                                       .type(STRING)
                                                       .description("지역 코드"),
                                               fieldWithPath("[].accommodations[].accommodationId")
                                                       .type(NUMBER)
                                                       .description("숙소 ID"),
                                               fieldWithPath("[].accommodations[].title")
                                                       .type(STRING)
                                                       .description("숙소 이름"),
                                               fieldWithPath("[].accommodations[].price")
                                                       .type(NUMBER)
                                                       .description("숙소 가격"),
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
                                       .responseSchema(schema("MainPageAccommodationsResponse"))
                                       .build()
                       )));
    }

    @Test
    @DisplayName("숙소 검색 조회 (페이징)")
    void getFilteredPagingAccommodations() throws Exception {
        //given
        List<FilteredAccommodationView> dtos = List.of(
                new FilteredAccommodationView(1L, "title-1", 50000, 4.3, 10,
                        List.of("https://example.com/a.jpg", "https://example.com/b.jpg"), false, null, null),
                new FilteredAccommodationView(2L, "title-2", 80000, 4.5, 23,
                        List.of("https://example.com/c.jpg", "https://example.com/d.jpg"), true, 1L, "my-wishlist-1")
        );

        PageResponse<FilteredAccommodationView> response = PageResponse.from(new PageImpl<>(
                dtos,
                PageRequest.of(0, 15),
                dtos.size()
        ));

        given(searchAccommodationsQueryUseCase.getFilteredPagingAccommodations(any(), any()))
                .willReturn(response);

        //when
        //then
        mockMvc.perform(get(("/api/accommodations/search"))
                       .param("areaCode", "32")
                       .param("amenities", "roomtv")
                       .param("amenities", "sports")
                       .param("priceGoe", "100000")
                       .param("priceLoe", "300000")
                       .param("page", "0")
                       .param("size", "15"))
               .andExpectAll(
                       handler().handlerType(AccommodationQueryController.class),
                       handler().methodName("getFilteredPagingAccommodations"),
                       status().isOk(),
                       jsonPath("$.contents.length()").value(dtos.size())
               )
               .andDo(document("search-accommodations",
                       resource(
                               builder()
                                       .tag(ACCOMMODATION_API_TAG)
                                       .summary("숙소 검색 조회")
                                       .queryParameters(
                                               parameterWithName("areaCode").optional().description("지역코드"),
                                               parameterWithName("amenities").optional().description("편의시설(다중 선택)"),
                                               parameterWithName("priceGoe").optional().description("숙소 최소 가격"),
                                               parameterWithName("priceLoe").optional().description("숙소 최대 가격"),
                                               parameterWithName("size").optional().description("페이지 크기"),
                                               parameterWithName("page").optional().description("페이지 번호 (0-index)")
                                       )
                                       .requestHeaders(headerWithName(AUTHORIZATION).optional().description("Bearer {액세스 토큰}"))
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
                                               fieldWithPath("contents[].accommodationId")
                                                       .type(NUMBER)
                                                       .description("숙소 ID"),
                                               fieldWithPath("contents[].title")
                                                       .type(STRING)
                                                       .description("숙소 제목"),
                                               fieldWithPath("contents[].price")
                                                       .type(NUMBER)
                                                       .description("숙소 가격"),
                                               fieldWithPath("contents[].avgRate")
                                                       .type(NUMBER)
                                                       .description("평균 평점"),
                                               fieldWithPath("contents[].reviewCount")
                                                       .type(NUMBER)
                                                       .description("리뷰 개수"),
                                               fieldWithPath("contents[].imageUrls")
                                                       .type(ARRAY)
                                                       .attributes(key("itemsType").value("string"))
                                                       .description("숙소의 이미지 목록(최대 10장)"),
                                               fieldWithPath("contents[].isInWishlist")
                                                       .type(BOOLEAN)
                                                       .description("위시리스트에 저장된 숙소인지 여부"),
                                               fieldWithPath("contents[].wishlistId")
                                                       .type(NUMBER)
                                                       .optional()
                                                       .description("저장된 위시리스트 ID (isInWishlist = true일 때만, false면 null)"),
                                               fieldWithPath("contents[].wishlistName")
                                                       .type(STRING)
                                                       .optional()
                                                       .description("저장된 위시리스트 이름 (isInWishlist = true일 때만, false면 null)")
                                       )
                                       .requestSchema(schema("QueryParameter-SearchAccommodationRequest"))
                                       .responseSchema(schema("PagingAccommodationsResponse"))
                                       .build()
                       )));
    }

    @Test
    @DisplayName("특정 숙소 상세 조회")
    void getAccommodation() throws Exception {
        //given
        Long accommodationId = 1L;
        AccommodationDetailView.DetailImageView detailImageDto = new AccommodationDetailView.DetailImageView(
                "https://example.com/thumbnail.jpg",
                List.of("https://example.com/a.jpg", "https://example.com/b.jpg")
        );
        List<String> amenities = List.of("바비큐장", "뷰티시설", "식음료장", "자전거대여");

        LocalDateTime now = LocalDateTime.now();
        List<AccommodationDetailView.DetailReviewView> reviewDtos = List.of(
                new AccommodationDetailView.DetailReviewView(1L, "member-A", "https://example.com/profile-A.jpg", now, now, 4.5, "review-content-1"),
                new AccommodationDetailView.DetailReviewView(2L, "member-B", "https://example.com/profile-B.jpg", now, now, 4.7, "review-content-2")
        );

        List<AccommodationDetailView.ReservedDateView> reservedDates = List.of(new AccommodationDetailView.ReservedDateView(now.minusDays(7).toLocalDate(), now.minusDays(5).toLocalDate()));
        AccommodationDetailView response = new AccommodationDetailView(accommodationId, "acc-title", 5, "경기도 부천시...", 35.3, 40.1,
                                                                       "10:00", "14:00", "acc-overview", "054-855-8552", "7일 이내 100%",
                                                                       55000, true, 1L, "my-wishlist-1", 4.8, detailImageDto, amenities, reviewDtos, reservedDates
        );

        given(getAccommodationDetailQueryUseCase.getDetailAccommodation(any(), any())).willReturn(response);

        //when
        //then
        mockMvc.perform(get("/api/accommodations/{id}", accommodationId))
               .andExpectAll(
                       handler().handlerType(AccommodationQueryController.class),
                       handler().methodName("getAccommodation"),
                       status().isOk(),
                       jsonPath("$.accommodationId").value(response.accommodationId()),
                       jsonPath("$.title").value(response.title()),
                       jsonPath("$.maxPeople").value(response.maxPeople()),
                       jsonPath("$.address").value(response.address()),
                       jsonPath("$.mapX").value(response.mapX()),
                       jsonPath("$.mapY").value(response.mapY()),
                       jsonPath("$.checkIn").value(response.checkIn()),
                       jsonPath("$.checkOut").value(response.checkOut()),
                       jsonPath("$.description").value(response.description()),
                       jsonPath("$.number").value(response.number()),
                       jsonPath("$.refundRegulation").value(response.refundRegulation()),
                       jsonPath("$.price").value(response.price()),
                       jsonPath("$.isInWishlist").value(response.isInWishlist()),
                       jsonPath("$.wishlistId").value(response.wishlistId()),
                       jsonPath("$.avgRate").value(response.avgRate()),
                       jsonPath("$.images.thumbnail").value(detailImageDto.thumbnail()),
                       jsonPath("$.images.others.length()").value(detailImageDto.others().size()),
                       jsonPath("$.amenities.length()").value(amenities.size()),
                       jsonPath("$.reviews.length()").value(reviewDtos.size()),
                       jsonPath("$.reservedDates.length()").value(reservedDates.size())
               )
               .andDo(document("get-detail-accommodation",
                       resource(
                               builder()
                                       .tag(ACCOMMODATION_API_TAG)
                                       .summary("특정 숙소 상세 조회")
                                       .pathParameters(parameterWithName("id").description("숙소 ID"))
                                       .requestHeaders(headerWithName(AUTHORIZATION).optional().description("Bearer {액세스 토큰}"))
                                       .responseFields(
                                               fieldWithPath("accommodationId")
                                                       .type(NUMBER)
                                                       .description("숙소 ID"),
                                               fieldWithPath("title")
                                                       .type(STRING)
                                                       .description("숙소 제목"),
                                               fieldWithPath("maxPeople")
                                                       .type(NUMBER)
                                                       .optional()
                                                       .description("숙소 최대 수용 인원"),
                                               fieldWithPath("address")
                                                       .type(STRING)
                                                       .description("숙소 주소"),
                                               fieldWithPath("mapX")
                                                       .type(NUMBER)
                                                       .description("숙소 X 좌표 값"),
                                               fieldWithPath("mapY")
                                                       .type(NUMBER)
                                                       .description("숙소 Y 좌표 값"),
                                               fieldWithPath("checkIn")
                                                       .type(STRING)
                                                       .optional()
                                                       .description("숙소 체크인 시간"),
                                               fieldWithPath("checkOut")
                                                       .type(STRING)
                                                       .optional()
                                                       .description("숙소 체크아웃 시간"),
                                               fieldWithPath("description")
                                                       .type(STRING)
                                                       .optional()
                                                       .description("숙소 상세 설명"),
                                               fieldWithPath("number")
                                                       .type(STRING)
                                                       .optional()
                                                       .description("숙소 번호"),
                                               fieldWithPath("refundRegulation")
                                                       .type(STRING)
                                                       .optional()
                                                       .description("숙소 환불 규정"),
                                               fieldWithPath("price")
                                                       .type(NUMBER)
                                                       .description("숙소 가격"),
                                               fieldWithPath("isInWishlist")
                                                       .type(BOOLEAN)
                                                       .description("위시리스트에 저장된 숙소인지 여부"),
                                               fieldWithPath("wishlistId")
                                                       .type(NUMBER)
                                                       .optional()
                                                       .description("저장된 위시리스트 ID (isInWishlist = true일 때만, false면 null)"),
                                               fieldWithPath("wishlistName")
                                                       .type(STRING)
                                                       .optional()
                                                       .description("저장된 위시리스트 이름 (isInWishlist = true일 때만, false면 null)"),
                                               fieldWithPath("avgRate")
                                                       .type(NUMBER)
                                                       .description("평균 평점"),
                                               fieldWithPath("images.thumbnail")
                                                       .type(STRING)
                                                       .description("숙소 썸네일 이미지"),
                                               fieldWithPath("images.others")
                                                       .type(ARRAY)
                                                       .attributes(key("itemsType").value("string"))
                                                       .description("숙소 썸네일 외 모든 이미지 목록"),
                                               fieldWithPath("amenities")
                                                       .type(ARRAY)
                                                       .attributes(key("itemsType").value("string"))
                                                       .description("숙소 보유 편의시설 목록"),
                                               fieldWithPath("reviews[].memberId")
                                                       .type(NUMBER)
                                                       .description("리뷰 작성자 ID"),
                                               fieldWithPath("reviews[].memberName")
                                                       .type(STRING)
                                                       .description("리뷰 작성자명"),
                                               fieldWithPath("reviews[].profileUrl")
                                                       .type(STRING)
                                                       .description("리뷰 작성자 프로필 이미지"),
                                               fieldWithPath("reviews[].memberCreatedDate")
                                                       .type(STRING)
                                                       .description("리뷰 작성자 가입일"),
                                               fieldWithPath("reviews[].reviewCreatedDate")
                                                       .type(STRING)
                                                       .description("리뷰 작성일"),
                                               fieldWithPath("reviews[].rating")
                                                       .type(NUMBER)
                                                       .description("리뷰 평점"),
                                               fieldWithPath("reviews[].content")
                                                       .type(STRING)
                                                       .description("리뷰 내용"),
                                               fieldWithPath("reservedDates[].start")
                                                       .type(STRING)
                                                       .description("선예약 시작일"),
                                               fieldWithPath("reservedDates[].end")
                                                       .type(STRING)
                                                       .description("선예약 종료일")
                                       )
                                       .responseSchema(schema("DetailAccommodationResponse"))
                                       .build()
                       )));
    }

    @Test
    @DisplayName("최근 조회 숙소 이력")
    @WithMockMember
    void getRecentViewAccommodations() throws Exception {
        //given
        LocalDateTime today = LocalDateTime.now();
        List<ViewHistoryAccommodationView> todays = List.of(
                new ViewHistoryAccommodationView(today.minusHours(1), 1L, "호텔A", 4.5, "https://example.com/a.jpg", true, 1L, "my-wishlist-1"),
                new ViewHistoryAccommodationView(today.minusHours(2), 2L, "호텔B", 4.8, "https://example.com/b.jpg", false, null, null),
                new ViewHistoryAccommodationView(today.minusHours(3), 3L, "호텔C", 4.3, "https://example.com/c.jpg", true, 3L, "my-wishlist-3")
        );

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<ViewHistoryAccommodationView> yesterdays = List.of(
                new ViewHistoryAccommodationView(yesterday.minusHours(1), 4L, "호텔D", 4.0, "https://example.com/d.jpg", false, null, null),
                new ViewHistoryAccommodationView(yesterday.minusHours(2), 5L, "호텔E", 3.9, "https://example.com/e.jpg", true, 5L, "my-wishlist-5")
        );

        List<ViewHistoryGroupView> result = List.of(new ViewHistoryGroupView(today.toLocalDate(), todays), new ViewHistoryGroupView(yesterday.toLocalDate(), yesterdays));
        given(getRecentViewAccommodationsQueryUseCase.getRecentViewAccommodations(any())).willReturn(result);

        //when
        //then
        mockMvc.perform(get("/api/accommodations/recent").header(AUTHORIZATION, "Bearer access-token"))
               .andExpectAll(
                       handler().handlerType(AccommodationQueryController.class),
                       handler().methodName("getRecentViewAccommodations"),
                       status().isOk(),
                       jsonPath("$.length()").value(result.size()),
                       jsonPath("$[0].accommodations", hasSize(result.get(0).accommodations().size())),
                       jsonPath("$[1].accommodations", hasSize(result.get(1).accommodations().size()))
               )
               .andDo(document("recent-view-accommodations",
                       resource(
                               builder()
                                       .tag(ACCOMMODATION_API_TAG)
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
                                       .build()
                       )));
    }

    @Test
    @DisplayName("숙소 특정 날짜 가격 조회")
    void getAccommodationPrice() throws Exception {
        //given
        Long accommodationId = 1L;
        LocalDate date = LocalDate.now();

        AccommodationPriceView result = new AccommodationPriceView(accommodationId, date, 130000);
        given(getAccommodationPriceQueryUseCase.getAccommodationPrice(any(), any())).willReturn(result);

        //when
        //then
        mockMvc.perform(get("/api/accommodations/{id}/price", accommodationId)
                       .param("date", date.toString())
               )
               .andExpectAll(
                       handler().handlerType(AccommodationQueryController.class),
                       handler().methodName("getAccommodationPrice"),
                       status().isOk(),
                       jsonPath("$.accommodationId").value(result.accommodationId()),
                       jsonPath("$.date").value(result.date().toString()),
                       jsonPath("$.price").value(result.price())
               )
               .andDo(document("get-accommodation-price",
                       resource(
                               builder()
                                       .tag(ACCOMMODATION_API_TAG)
                                       .summary("숙소 특정 날짜 가격 조회")
                                       .pathParameters(parameterWithName("id").description("숙소 ID"))
                                       .queryParameters(parameterWithName("date").description("조회 날짜(yyyy-MM-dd)"))
                                       .responseFields(
                                               fieldWithPath("accommodationId")
                                                       .type(NUMBER)
                                                       .description("숙소 ID"),
                                               fieldWithPath("date")
                                                       .type(STRING)
                                                       .description("조회 날짜"),
                                               fieldWithPath("price")
                                                       .type(NUMBER)
                                                       .description("가격")
                                       )
                                       .responseSchema(schema("AccommodationPriceResponse"))
                                       .build()
                       )));
    }
}
