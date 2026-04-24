package project.accommodation.adapter.in.web.response;

import project.accommodation.adapter.out.persistence.model.MainAccListQueryDto;

/**
 * 메인 화면 지역별 각 숙소 최소 정보
 */
public record MainAccListResponse(
        Long accommodationId,
        String title,
        int price,
        double avgRate,
        String thumbnailUrl,
        boolean isInWishlist,
        String wishlistName,
        Long wishlistId) {

    public static MainAccListResponse from(MainAccListQueryDto queryDto) {
        return new MainAccListResponse(
                queryDto.accommodationId(),
                queryDto.title(),
                queryDto.price(),
                queryDto.avgRate(),
                queryDto.thumbnailUrl(),
                queryDto.isInWishlist(),
                queryDto.wishlistName(),
                queryDto.wishlistId()
        );
    }
}
