package project.member.application.in.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.member.application.in.query.model.DefaultProfileView;
import project.member.application.in.query.model.TripHistoryView;

public interface ReadMemberProfileUseCase {

    DefaultProfileView getMyProfile(Long memberId);

    Page<TripHistoryView> getTripsHistory(Long memberId, Pageable pageable);
}
