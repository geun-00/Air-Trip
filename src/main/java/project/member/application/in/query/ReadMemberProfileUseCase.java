package project.member.application.in.query;

import org.springframework.data.domain.Pageable;
import project.common.adapter.in.web.response.PageResponse;
import project.member.application.in.query.model.DefaultProfileView;
import project.member.application.in.query.model.TripHistoryView;

public interface ReadMemberProfileUseCase {

    DefaultProfileView getMyProfile(Long memberId);

    PageResponse<TripHistoryView> getTripsHistory(Long memberId, Pageable pageable);
}
