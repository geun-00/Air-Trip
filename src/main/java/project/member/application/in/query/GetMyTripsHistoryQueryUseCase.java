package project.member.application.in.query;

import org.springframework.data.domain.Pageable;
import project.common.adapter.in.web.response.PageResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;

public interface GetMyTripsHistoryQueryUseCase {

    PageResponse<TripHistoryResponse> getTripsHistory(Long memberId, Pageable pageable);
}
