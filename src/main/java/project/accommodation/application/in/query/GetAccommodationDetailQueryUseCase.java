package project.accommodation.application.in.query;

import project.accommodation.adapter.in.web.response.DetailAccommodationResDto;

public interface GetAccommodationDetailQueryUseCase {

    DetailAccommodationResDto getDetailAccommodation(Long accId, Long memberId);
}
