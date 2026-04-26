package project.accommodation.application.in.query;

import project.accommodation.adapter.in.web.response.MainAccResDto;

import java.util.List;

public interface GetMainAccommodationsQueryUseCase {

    List<MainAccResDto> getAccommodations(Long memberId);
}
