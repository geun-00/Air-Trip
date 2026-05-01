package project.accommodation.application.out.query;

import project.accommodation.application.out.query.model.WishlistInfoView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReadAccommodationWishlistPort {

    Optional<WishlistInfoView> findByAccommodationIdAndMemberId(Long accommodationId, Long memberId);

    Map<Long, WishlistInfoView> getAllByAccommodationIdsAndMemberId(List<Long> accommodationIds, Long memberId);
}
