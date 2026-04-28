package project.member.application.in.query;

import project.member.application.in.query.model.ViewHistoryGroupView;

import java.util.List;

public interface GetRecentViewAccommodationsQueryUseCase {

    List<ViewHistoryGroupView> getRecentViewAccommodations(Long memberId);
}
