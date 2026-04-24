package project.member.application.out.query;

import org.springframework.data.domain.Pageable;
import project.common.adapter.in.web.response.PageResponse;
import project.member.application.query.model.TripHistoryView;

public interface GetMemberTripsHistoryPort {

    PageResponse<TripHistoryView> getTripsHistory(Long memberId, Pageable pageable);
}
