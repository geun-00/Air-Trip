package project.wishlist.adapter.in.web.response;

import java.util.List;

public record WishlistDetailResponse(Long accommodationId, String wishlistName, String title, String description,
                                     double mapX, double mapY, double avgRate, List<String> imageUrls, String memo) {
}
