package project.member.application.out.query;

import org.springframework.data.domain.Pageable;
import project.common.adapter.in.web.response.PageResponse;
import project.member.application.in.query.model.DefaultProfileView;
import project.member.application.in.query.model.TripHistoryView;

public interface ReadMemberProfilePort {

    DefaultProfileView getDefaultProfile(Long memberId);

    PageResponse<TripHistoryView> getTripsHistory(Long memberId, Pageable pageable);
}
