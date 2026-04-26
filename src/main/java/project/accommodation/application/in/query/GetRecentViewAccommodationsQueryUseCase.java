package project.accommodation.application.in.query;

import project.accommodation.adapter.in.web.response.ViewHistoryResDto;

import java.util.List;

public interface GetRecentViewAccommodationsQueryUseCase {

    List<ViewHistoryResDto> getRecentViewAccommodations(Long memberId);
}
