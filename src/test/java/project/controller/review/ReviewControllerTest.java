package project.controller.review;

import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.SimpleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import project.common.adapter.in.web.response.PageResponse;
import project.controller.RestDocsTestSupport;
import project.review.adapter.in.web.ReviewCommandController;
import project.review.adapter.in.web.ReviewQueryController;
import project.review.adapter.in.web.request.PostReviewRequest;
import project.review.adapter.in.web.request.UpdateReviewRequest;
import project.review.application.in.command.CreateReviewUseCase;
import project.review.application.in.command.DeleteReviewUseCase;
import project.review.application.in.command.UpdateReviewUseCase;
import project.review.application.in.query.GetMyReviewsQueryUseCase;
import project.review.application.in.query.model.MyReviewView;
import project.security.WithMockMember;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static com.epages.restdocs.apispec.Schema.schema;
import static com.epages.restdocs.apispec.SimpleType.NUMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ReviewCommandController.class, ReviewQueryController.class})
class ReviewControllerTest extends RestDocsTestSupport {

    private static final String REVIEW_API_TAG = "Review API";

    @MockitoBean
    CreateReviewUseCase createReviewUseCase;

    @MockitoBean
    UpdateReviewUseCase updateReviewUseCase;

    @MockitoBean
    DeleteReviewUseCase deleteReviewUseCase;

    @MockitoBean
    GetMyReviewsQueryUseCase getMyReviewsQueryUseCase;

    @Test
    @DisplayName("등록한 후기 목록 조회")
    @WithMockMember
    void getMyReviews() throws Exception {
        LocalDate now = LocalDate.now();
        List<MyReviewView> views = List.of(
                new MyReviewView(1L, 1L, "https://example-a.com", "title-A", "content-A", 3.0, now.minusDays(14)),
                new MyReviewView(2L, 2L, "https://example-b.com", "title-B", "content-B", 4.0, now.minusDays(10)),
                new MyReviewView(3L, 3L, "https://example-c.com", "title-C", "content-C", 4.5, now.minusDays(7))
        );
        PageResponse<MyReviewView> response = PageResponse.from(new PageImpl<>(
                views,
                PageRequest.of(0, 10),
                views.size()
        ));
        given(getMyReviewsQueryUseCase.getMyReviews(anyLong(), any()))
                .willReturn(response);

        mockMvc.perform(get("/api/reviews/me")
                       .header(AUTHORIZATION, "Bearer {access-token}")
                       .param("page", "0")
                       .param("size", "10")
               )
               .andExpectAll(
                       handler().handlerType(ReviewQueryController.class),
                       handler().methodName("getMyReviews"),
                       status().isOk(),
                       jsonPath("$.contents.length()").value(views.size())
               )
               .andDo(document("get-my-reviews",
                       resource(
                               builder()
                                       .tag(REVIEW_API_TAG)
                                       .summary("등록한 후기 목록 조회")
                                       .requestHeaders(headerWithName(AUTHORIZATION).description("Bearer {액세스 토큰}"))
                                       .queryParameters(
                                               parameterWithName("size").optional().description("페이지 크기"),
                                               parameterWithName("page").optional().description("페이지 번호 (0-index)")
                                       )
                                       .responseFields(
                                               fieldWithPath("contents").type(ARRAY).description("검색 페이지 데이터"),
                                               fieldWithPath("hasPrev").type(BOOLEAN).description("이전 페이지 존재 여부"),
                                               fieldWithPath("hasNext").type(BOOLEAN).description("다음 페이지 존재 여부"),
                                               fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("검색된 전체 데이터 개수"),
                                               fieldWithPath("prevPage").type(JsonFieldType.NUMBER).description("이전 페이지 번호 (0-index, 없으면 -1)"),
                                               fieldWithPath("nextPage").type(JsonFieldType.NUMBER).description("다음 페이지 번호 (0-index, 없으면 -1)"),
                                               fieldWithPath("totalPage").type(JsonFieldType.NUMBER).description("총 페이지 개수"),
                                               fieldWithPath("current").type(JsonFieldType.NUMBER).description("현재 페이지 번호 (0-index)"),
                                               fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                               fieldWithPath("contents[].reviewId").type(JsonFieldType.NUMBER).description("후기 ID"),
                                               fieldWithPath("contents[].accommodationId").type(JsonFieldType.NUMBER).description("숙소 ID"),
                                               fieldWithPath("contents[].thumbnailUrl").type(STRING).description("숙소 썸네일 URL"),
                                               fieldWithPath("contents[].title").type(STRING).description("숙소 제목"),
                                               fieldWithPath("contents[].content").type(STRING).description("리뷰 내용"),
                                               fieldWithPath("contents[].rating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                                               fieldWithPath("contents[].createdDate").type(STRING).description("리뷰 등록일")
                                       )
                                       .responseSchema(schema("MyReviewResponse"))
                                       .build()
                       ))
               );
    }

    @Test
    @DisplayName("리뷰 등록")
    @WithMockMember
    void createReview() throws Exception {
        PostReviewRequest request = new PostReviewRequest(BigDecimal.valueOf(4.5), "만족스러운 여행이었어요!");

        mockMvc.perform(post("/api/reviews/reservations/{reservationId}", 1L)
                       .header(AUTHORIZATION, "Bearer {access-token}")
                       .contentType(MediaType.APPLICATION_JSON_VALUE)
                       .content(creatJson(request)))
               .andExpectAll(
                       handler().handlerType(ReviewCommandController.class),
                       handler().methodName("createReview"),
                       status().isCreated()
               )
               .andDo(document("create-review",
                       resource(
                               builder()
                                       .tag(REVIEW_API_TAG)
                                       .summary("리뷰 등록")
                                       .requestHeaders(headerWithName(AUTHORIZATION).description("Bearer {액세스 토큰}"))
                                       .pathParameters(ResourceDocumentation.parameterWithName("reservationId")
                                                                            .type(SimpleType.NUMBER)
                                                                            .description("리뷰를 등록할 예약 ID"))
                                       .requestFields(
                                               fieldWithPath("rating").description("별점 (0.0 ~ 5.0)").type(NUMBER),
                                               fieldWithPath("content").description("내용 (최대 100자)").type(STRING)
                                       )
                                       .requestSchema(schema("PostReviewRequest"))
                                       .build()
                       )
               ));
    }

    @Test
    @DisplayName("등록한 후기 수정")
    @WithMockMember
    void updateReview() throws Exception {
        UpdateReviewRequest request = new UpdateReviewRequest(BigDecimal.valueOf(4.5), "만족스러운 여행이었어요!");

        mockMvc.perform(put("/api/reviews/{reviewId}", 1L)
                       .header(AUTHORIZATION, "Bearer {access-token}")
                       .contentType(MediaType.APPLICATION_JSON_VALUE)
                       .content(creatJson(request)))
               .andExpectAll(
                       handler().handlerType(ReviewCommandController.class),
                       handler().methodName("updateReview"),
                       status().isOk()
               )
               .andDo(document("update-review",
                       resource(
                               builder()
                                       .tag(REVIEW_API_TAG)
                                       .summary("리뷰 수정")
                                       .requestHeaders(headerWithName(AUTHORIZATION).description("Bearer {액세스 토큰}"))
                                       .pathParameters(ResourceDocumentation.parameterWithName("reviewId")
                                                                            .type(SimpleType.NUMBER)
                                                                            .description("수정할 리뷰 ID"))
                                       .requestFields(
                                               fieldWithPath("rating").description("별점 (0.0 ~ 5.0)").type(NUMBER),
                                               fieldWithPath("content").description("내용 (최대 100자)").type(STRING)
                                       )
                                       .requestSchema(schema("UpdateReviewRequest"))
                                       .build()
                       )
               ));
    }

    @Test
    @DisplayName("등록한 후기 삭제")
    @WithMockMember
    void deleteReview() throws Exception {
        mockMvc.perform(delete("/api/reviews/{reviewId}", 1L)
                       .header(AUTHORIZATION, "Bearer {access-token}")
                       .contentType(MediaType.APPLICATION_JSON_VALUE)
               )
               .andExpectAll(
                       handler().handlerType(ReviewCommandController.class),
                       handler().methodName("deleteReview"),
                       status().isOk()
               )
               .andDo(document("delete-review",
                       resource(
                               builder()
                                       .tag(REVIEW_API_TAG)
                                       .summary("리뷰 삭제")
                                       .requestHeaders(headerWithName(AUTHORIZATION).description("Bearer {액세스 토큰}"))
                                       .pathParameters(ResourceDocumentation.parameterWithName("reviewId")
                                                                            .type(SimpleType.NUMBER)
                                                                            .description("삭제할 리뷰 ID"))
                                       .build()
                       )
               ));
    }
}
