package project.wishlist.adapter.in.web.response;

import project.wishlist.adapter.out.persistence.model.WishlistDetailQueryDto;

import java.util.List;

public record WishlistDetailResponse(
        Long accommodationId,
        String wishlistName,
        String title,
        String description,
        double mapX,
        double mapY,
        double avgRate,
        List<String> imageUrls,
        String memo) {

    public static WishlistDetailResponse from(WishlistDetailQueryDto queryDto, List<String> imageUrls) {
        return new WishlistDetailResponse(
                queryDto.accommodationId(),
                queryDto.wishlistName(),
                queryDto.title(),
                queryDto.description(),
                queryDto.mapX(),
                queryDto.mapY(),
                queryDto.avgRate(),
                imageUrls,
                queryDto.memo()
        );
    }
}
