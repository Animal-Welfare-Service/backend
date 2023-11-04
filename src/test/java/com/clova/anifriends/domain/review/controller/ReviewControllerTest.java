package com.clova.anifriends.domain.review.controller;

import static com.clova.anifriends.domain.recruitment.support.fixture.RecruitmentFixture.recruitment;
import static com.clova.anifriends.domain.review.support.ReviewDtoFixture.findReviewResponse;
import static com.clova.anifriends.domain.review.support.ReviewFixture.review;
import static com.clova.anifriends.domain.shelter.support.ShelterFixture.shelter;
import static com.clova.anifriends.domain.volunteer.support.VolunteerFixture.volunteer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.clova.anifriends.base.BaseControllerTest;
import com.clova.anifriends.domain.recruitment.Recruitment;
import com.clova.anifriends.domain.review.Review;
import com.clova.anifriends.domain.review.dto.response.FindReviewResponse;
import com.clova.anifriends.domain.review.dto.response.FindShelterReviewsResponse;
import com.clova.anifriends.domain.shelter.Shelter;
import com.clova.anifriends.domain.shelter.ShelterImage;
import com.clova.anifriends.domain.shelter.support.ShelterImageFixture;
import com.clova.anifriends.domain.volunteer.Volunteer;
import com.clova.anifriends.domain.volunteer.VolunteerImage;
import com.clova.anifriends.domain.volunteer.support.VolunteerImageFixture;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;

class ReviewControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("성공")
    void findReview() throws Exception {
        //given
        Shelter shelter = shelter();
        Volunteer volunteer = volunteer();
        Recruitment recruitment = recruitment(shelter);
        long reviewId = 1L;
        Review review = review(recruitment, volunteer);
        ReflectionTestUtils.setField(review, "reviewId", reviewId);
        FindReviewResponse response = findReviewResponse(review);

        when(reviewService.findReview(anyLong(), eq(reviewId)))
            .thenReturn(response);

        //when
        ResultActions result = mockMvc.perform(
            get("/api/volunteers/reviews/{reviewId}", reviewId)
                .header(AUTHORIZATION, volunteerAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
            .andDo(restDocs.document(
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("봉사자 액세스 토큰")
                ),
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 ID")
                ),
                responseFields(
                    fieldWithPath("content").type(STRING).description("리뷰 내용"),
                    fieldWithPath("shelterName").type(STRING).description("보호소 이름"),
                    fieldWithPath("shelterImageUrl").type(STRING).description("보호소 이미지 url")
                        .optional(),
                    fieldWithPath("imageUrls[]").type(ARRAY).description("리뷰 이미지 url 리스트")
                )
            ));
    }

    @Test
    @DisplayName("성공: 보호소가 받은 봉사자 리뷰 목록 조회 api 호출")
    void findShelterReviews() throws Exception {
        //given
        Long shelterId = 1L;
        Shelter shelter = shelter();
        ShelterImage shelterImage = ShelterImageFixture.shelterImage(shelter);
        shelter.updateShelterImage(shelterImage);
        Recruitment recruitment = recruitment(shelter);
        Volunteer volunteer = volunteer();
        VolunteerImage volunteerImage = VolunteerImageFixture.volunteerImage(volunteer);
        volunteer.updateVolunteerImage(volunteerImage);
        Review review = review(recruitment, volunteer);
        ReflectionTestUtils.setField(review, "reviewId", 1L);
        ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now());
        PageImpl<Review> reviewPage = new PageImpl<>(List.of(review));
        FindShelterReviewsResponse response = FindShelterReviewsResponse.from(reviewPage);

        given(reviewService.findShelterReviews(anyLong(), any())).willReturn(response);

        //when
        ResultActions resultActions
            = mockMvc.perform(get("/api/shelters/{shelterId}/reviews", shelterId)
            .header(AUTHORIZATION, volunteerAccessToken)
            .param("pageNumber", "0")
            .param("pageSize", "10"));

        //then
        resultActions.andExpect(status().isOk())
            .andDo(restDocs.document(
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("봉사자 액세스 토큰")
                ),
                pathParameters(
                    parameterWithName("shelterId").description("보호소 ID")
                ),
                queryParameters(
                    parameterWithName("pageNumber").description("페이지 번호"),
                    parameterWithName("pageSize").description("페이지 사이즈")
                ),
                responseFields(
                    fieldWithPath("reviews").type(ARRAY).description("리뷰 리스트"),
                    fieldWithPath("reviews[].reviewId").type(NUMBER).description("리뷰 ID"),
                    fieldWithPath("reviews[].createdAt").type(STRING).description("리뷰 생성일"),
                    fieldWithPath("reviews[].content").type(STRING).description("리뷰 내용"),
                    fieldWithPath("reviews[].reviewImageUrls").type(ARRAY)
                        .description("리뷰 이미지 url 리스트"),
                    fieldWithPath("reviews[].volunteerName").type(STRING).description("봉사자 이름"),
                    fieldWithPath("reviews[].temperature").type(NUMBER).description("봉사자 온도"),
                    fieldWithPath("reviews[].volunteerImageUrl").type(STRING)
                        .description("봉사자 프로필 이미지 url"),
                    fieldWithPath("reviews[].VolunteerReviewCount").type(NUMBER)
                        .description("봉사자 리뷰 수"),
                    fieldWithPath("pageInfo").type(OBJECT).description("페이지 정보"),
                    fieldWithPath("pageInfo.totalElements").type(NUMBER).description("총 요소 개수"),
                    fieldWithPath("pageInfo.hasNext").type(BOOLEAN).description("다음 페이지 여부")
                )
            ));
    }
}
