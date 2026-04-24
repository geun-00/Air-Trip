package project.member.application.in.query;

import org.springframework.data.domain.Pageable;
import project.common.adapter.in.web.response.PageResponse;
import project.member.application.query.model.TripHistoryView;

public interface GetMyTripsHistoryQueryUseCase {

    PageResponse<TripHistoryView> getTripsHistory(Long memberId, Pageable pageable);
}
