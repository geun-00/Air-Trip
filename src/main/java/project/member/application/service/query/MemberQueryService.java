package project.member.application.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.member.application.in.query.ReadMemberProfileUseCase;
import project.member.application.in.query.SearchMembersUseCase;
import project.member.application.in.query.model.ChatMembersSearchView;
import project.member.application.in.query.model.DefaultProfileView;
import project.member.application.in.query.model.TripHistoryView;
import project.member.application.out.query.ReadMemberProfilePort;
import project.member.application.out.query.SearchMembersPort;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService implements ReadMemberProfileUseCase,
                                           SearchMembersUseCase {

    private final SearchMembersPort searchMembersPort;
    private final ReadMemberProfilePort readMemberProfilePort;

    @Override
    public DefaultProfileView getMyProfile(Long memberId) {
        return readMemberProfilePort.getDefaultProfile(memberId);
    }

    @Override
    public ChatMembersSearchView findMembersByName(String name) {
        return searchMembersPort.findMembersByName(name);
    }

    @Override
    public Page<TripHistoryView> getTripsHistory(Long memberId, Pageable pageable) {
        return readMemberProfilePort.getTripsHistory(memberId, pageable);
    }
}
