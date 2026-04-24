package project.member.application.out.query;

import org.springframework.data.domain.Pageable;
import project.common.adapter.in.web.response.PageResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;

public interface GetMemberTripsHistoryPort {

    PageResponse<TripHistoryResponse> getTripsHistory(Long memberId, Pageable pageable);
}
