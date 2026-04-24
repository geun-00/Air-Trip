package project.accommodation.adapter.in.web.response;

import java.util.List;

/**
 * 메인 화면 숙소 조회 DTO
 */
public record MainAccResDto(
        String areaName,
        String areaCode,
        List<MainAccListResponse> accommodations) {
}
