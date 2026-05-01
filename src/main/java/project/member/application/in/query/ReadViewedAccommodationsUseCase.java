package project.member.application.in.query;

import project.member.application.in.query.model.ViewHistoryGroupView;

import java.util.List;

public interface ReadViewedAccommodationsUseCase {

    List<ViewHistoryGroupView> getRecentViewAccommodations(Long memberId);
}
