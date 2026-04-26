package project.accommodation.application.in.query;

import project.accommodation.application.in.query.model.ViewHistoryGroupView;

import java.util.List;

public interface GetRecentViewAccommodationsQueryUseCase {

    List<ViewHistoryGroupView> getRecentViewAccommodations(Long memberId);
}
